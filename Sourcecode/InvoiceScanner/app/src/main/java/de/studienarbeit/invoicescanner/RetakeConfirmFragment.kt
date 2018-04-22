package de.studienarbeit.invoicescanner

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



/**
 * Created by andym on 20.04.2018.
 */
class RetakeConfirmFragment() : Fragment() , View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback
{

    lateinit var mListener : onButtonClickedListener

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dismiss -> mListener.onButtonDismiss()
            R.id.analyze -> mListener.onButtonAnalyze()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                                       container: ViewGroup?,
                                       savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.confirm_retake_fragment, container, false)

    interface onButtonClickedListener {
        fun onButtonDismiss();
        fun onButtonAnalyze();
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

            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

            view.myimage.setImageBitmap(myBitmap)
        }
        view.findViewById<View>(R.id.dismiss).setOnClickListener(this)
        view.findViewById<View>(R.id.analyze).setOnClickListener(this)
    }
}