package de.studienarbeit.invoicescanner.Fragments

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
import de.studienarbeit.invoicescanner.Invoice
import de.studienarbeit.invoicescanner.R
import kotlinx.android.synthetic.main.fragment_retake_confirm.view.*


/**
 * Created by andym on 20.04.2018.
 */
class RetakeConfirmFragment : Fragment() , View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback
{
    private lateinit var mListener : OnButtonClickedListener

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dismiss -> mListener.onButtonDismiss()
            R.id.analyze -> mListener.onButtonAnalyze()
        }
    }
    override fun onCreateView(inflater: LayoutInflater,
                                       container: ViewGroup?,
                                       savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_retake_confirm, container, false)

    interface OnButtonClickedListener {
        fun onButtonDismiss()
        fun onButtonAnalyze()
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
        val imgFile = File(arguments.get("imagepath") as String)

        if (imgFile.exists()) {

            val currentImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            view.myimage.setImageBitmap(currentImage)
        }

        view.findViewById<View>(R.id.dismiss).setOnClickListener(this)
        view.findViewById<View>(R.id.analyze).setOnClickListener(this)
    }
}