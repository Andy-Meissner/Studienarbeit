package de.studienarbeit.invoicescanner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.SparseArray
import com.google.android.gms.common.util.IOUtils
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.File
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.BufferedReader
import java.io.FileOutputStream
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.os.AsyncTask
import android.util.Log
import info.debatty.java.stringsimilarity.JaroWinkler
import nl.garvelink.iban.IBAN
import nl.garvelink.iban.IBANFields
import java.util.regex.Pattern


class ImageAnalyzer(context: Context, imagePath : String) {

    private val context = context
    private val imagePath = imagePath

    private lateinit var imgFile : File
    private lateinit var imgBitmap : Bitmap
    private lateinit var recognizedText : SparseArray<TextBlock>
    private lateinit var tessText : String
    private lateinit var gmvText : String
    private lateinit var invoice : Invoice
    private var analyzeTimeTess : Long = 0
    private var anaylzeTimeGMV : Long = 0

    private fun loadImage()
    {
        imgFile = File(imagePath)
        if (imgFile.exists()) {
            imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        }
    }

    private fun getTextFromImage()
    {
        val time1 = System.nanoTime()

        val textRecognizer : TextRecognizer = TextRecognizer.Builder(context).build()
        if(imgFile.exists() && textRecognizer.isOperational())
        {
            val myFrame = Frame.Builder()
            myFrame.setBitmap(imgBitmap)
            recognizedText = textRecognizer.detect(myFrame.build())
        }
        else
        {
            Log.w("ImageAnalyze:","no image found or textrecognizer not operational")
        }

        val time2 = System.nanoTime()
        anaylzeTimeGMV = time2 - time1
    }

    private fun getTextFromImageTess()
    {
        var m = context.packageManager
        var path = context.packageName
        try {
            val p = m.getPackageInfo(path, 0)
            path = p.applicationInfo.dataDir
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("IA:", "Error Package name not found ", e)
        }

        val tessdataDir = File(path + "/tessdata")
        if (!tessdataDir.exists())
        {
            tessdataDir.mkdirs()
        }
        val trainedData = File(path + "/tessdata/deu.traineddata")
        if (trainedData.exists())
        {
            if (trainedData.isDirectory)
            {
                trainedData.delete()
                trainedData.createNewFile()
            }
        }
        else
        {
            trainedData.createNewFile()
        }

        val outputStream = FileOutputStream(trainedData)
        IOUtils.copyStream(context.assets.open("deu.traineddata"), outputStream)
        outputStream.close()

        if(imgFile.exists()) {
            var time1 = 0L
            var time2 = 0L
            object : AsyncTask<Void, Void, Int>() {
                override fun doInBackground(vararg params: Void): Int? {
                    time1 = System.nanoTime()
                    val baseApi = TessBaseAPI()
                    baseApi.init(path, "deu") // myDir + "/tessdata/eng.traineddata" must be present
                    baseApi.setImage(imgFile)

                    tessText = baseApi.utF8Text // Log or otherwise display this string...

                    baseApi.end()
                    time2 = System.nanoTime()
                    return 0
                }

                override fun onPostExecute(resultCode: Int?) {
                    analyzeTimeTess = time2 - time1
                    debugLol()
                }
            }.execute()
        }
    }

    private fun debugLol()
    {
        var mystop = 1+1
    }
    private fun getImagePath(): String {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

        val myDir = File("$root/invoice_scanner")
        myDir.mkdirs()
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val imagepath = myDir.toString() + "Invoice-" + timestamp + ".jpg"

        return imagepath
    }

    private fun mapTextToInvoice()
    {
        gmvText = ""
        var ibanPattern = Pattern.compile("^DE\\d{20}\$")
        var bicPattern = Pattern.compile("^[a-zA-Z]{7}[0-9]{1}[a-zA-Z]{3}\$")
        var iban = ""
        var bic = ""
        var amount = 0.0
        var details = ""
        var receiver = ""


        for (i in 0 until recognizedText.size())
        {
            if (recognizedText[i] != null) {
                gmvText += recognizedText[i].value + "\n"
                var vals = recognizedText[i].value.split(" ")
                for (j in 0 until vals.size)
                {
                    var curString = vals[j]
                    //var myIban = IBAN.valueOf(curString)
                    //IBANFields.getBankIdentifier(myIban)
                    var m = ibanPattern.matcher(curString)
                    var m2 = bicPattern.matcher(curString)
                    if (m.matches()) {
                        iban = curString
                    }
                    else if (m2.matches()) {
                        bic = curString
                    }
                    else
                    {
                        details += curString + "\n"
                    }
                }

            }


        }
        invoice = Invoice(null, getImagePath(), iban, bic, 0.0, details, receiver , false)
    }

    fun analyse()
    {
        loadImage()
        getTextFromImage()
        getTextFromImageTess()
        mapTextToInvoice()
    }

    fun getInvoice() : Invoice
    {
        return invoice
    }
}