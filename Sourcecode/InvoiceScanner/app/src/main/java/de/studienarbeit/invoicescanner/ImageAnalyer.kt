package de.studienarbeit.invoicescanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.File

class ImageAnalyer(context: Context , imagePath : String) {

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
            val texts = textRecognizer.detect(myFrame.build())

        }
    }

    private fun mapTextToInvoice()
    {
        var mystring = ""
        for (i in 0 until recognizedText.size())
        {
            mystring += recognizedText[i]?.value
        }
        invoice = Invoice(imagePath, mystring, mystring, 0.0, mystring, mystring, false)
    }

    fun analyze()
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