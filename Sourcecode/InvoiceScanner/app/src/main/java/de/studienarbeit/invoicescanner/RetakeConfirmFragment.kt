package de.studienarbeit.invoicescanner

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.confirm_retake_fragment.view.*
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.google.android.gms.vision.text.TextRecognizer
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import java.util.EnumSet.range


/**
 * Created by andym on 20.04.2018.
 */
class RetakeConfirmFragment() : Fragment() , View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback
{
    lateinit var currentImage : Bitmap
    lateinit var mListener : onButtonClickedListener

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dismiss -> mListener.onButtonDismiss()
            R.id.analyze -> analysePhoto()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                                       container: ViewGroup?,
                                       savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.confirm_retake_fragment, container, false)

    interface onButtonClickedListener {
        fun onButtonDismiss();
        fun onButtonAnalyze(string: String);
    }

    fun analysePhoto()
    {
        val textRecognizer : TextRecognizer = TextRecognizer.Builder(activity).build()
        val myFrame = Frame.Builder()
        myFrame.setBitmap(currentImage)
        val texts = textRecognizer.detect(myFrame.build())
        var mystring = ""
        for (i in 0..texts.size()-1)
        {
            mystring += texts[i]?.value
        }



        mListener.onButtonAnalyze(mystring)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as onButtonClickedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnArticleSelectedListener")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imagePath = arguments.get("imagepath") as String
        val imgFile = File(imagePath)

        if (imgFile.exists()) {

            currentImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            view.myimage.setImageBitmap(currentImage)
        }
        view.findViewById<View>(R.id.dismiss).setOnClickListener(this)
        view.findViewById<View>(R.id.analyze).setOnClickListener(this)
    }
}