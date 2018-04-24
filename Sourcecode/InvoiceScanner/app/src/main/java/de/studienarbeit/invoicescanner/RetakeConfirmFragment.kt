package de.studienarbeit.invoicescanner

import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.gms.vision.Frame
import kotlinx.android.synthetic.main.fragment_retake_confirm.view.*


/**
 * Created by andym on 20.04.2018.
 */
class RetakeConfirmFragment : Fragment() , View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback
{
    private lateinit var currentImage : Bitmap
    private lateinit var mListener : OnButtonClickedListener
    private lateinit var imagePath : String

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dismiss -> mListener.onButtonDismiss()
            R.id.analyze -> analysePhoto()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                                       container: ViewGroup?,
                                       savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_retake_confirm, container, false)

    interface OnButtonClickedListener {
        fun onButtonDismiss()
        fun onButtonAnalyze(invoice: Invoice)
    }

    private fun analysePhoto()
    {
        val textRecognizer : TextRecognizer = TextRecognizer.Builder(activity).build()
        val myFrame = Frame.Builder()
        myFrame.setBitmap(currentImage)
        val texts = textRecognizer.detect(myFrame.build())
        var mystring = ""
        for (i in 0 until texts.size())
        {
            mystring += texts[i]?.value
        }

        val analyzedInvoice = Invoice(imagePath,mystring,mystring,0.0 ,mystring,mystring,false)


        mListener.onButtonAnalyze(analyzedInvoice)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as OnButtonClickedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnArticleSelectedListener")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imagePath = arguments.get("imagepath") as String
        val imgFile = File(imagePath)

        if (imgFile.exists()) {

            currentImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            view.myimage.setImageBitmap(currentImage)
        }
        view.findViewById<View>(R.id.dismiss).setOnClickListener(this)
        view.findViewById<View>(R.id.analyze).setOnClickListener(this)
    }
}