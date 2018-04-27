package de.studienarbeit.invoicescanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.File
import java.util.*

class ImageAnalyzer(context: Context, imagePath : String) {

    private val context = context
    private val imagePath = imagePath

    private lateinit var imgFile : File
    private lateinit var imgBitmap : Bitmap
    private lateinit var recognizedText : SparseArray<TextBlock>
    private lateinit var invoice : Invoice

    private fun loadImage()
    {
        imgFile = File(imagePath)
        if (imgFile.exists()) {
            imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        }
    }

    private fun getTextFromImage()
    {
        val textRecognizer : TextRecognizer = TextRecognizer.Builder(context).build()
        if(imgFile.exists())
        {
            val myFrame = Frame.Builder()
            myFrame.setBitmap(imgBitmap)
            recognizedText = textRecognizer.detect(myFrame.build())

        }
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
        var mystring = ""
        for (i in 0 until recognizedText.size())
        {
            mystring += recognizedText[i]?.value
        }
        invoice = Invoice(null, getImagePath(), mystring, mystring, 0.0, mystring, mystring, false)
    }

    fun analyse()
    {
        loadImage()
        getTextFromImage()
        mapTextToInvoice()
    }

    fun getInvoice() : Invoice
    {
        return invoice
    }
}