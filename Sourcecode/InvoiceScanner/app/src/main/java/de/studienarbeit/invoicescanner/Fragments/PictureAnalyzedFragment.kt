package de.studienarbeit.invoicescanner.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.studienarbeit.invoicescanner.R
import kotlinx.android.synthetic.main.fragment_picture_analyzed.view.*
import java.io.File

class PictureAnalyzedFragment : Fragment() {
    lateinit var currentImage : Bitmap

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View? = inflater!!.inflate(R.layout.fragment_picture_analyzed, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagePath = arguments.get("imagepath") as String
        view?.edit_iban?.setText(arguments.get("text") as String , TextView.BufferType.EDITABLE)

        val imgFile = File(imagePath)

        if (imgFile.exists()) {

            currentImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            view?.captured_image?.setImageBitmap(currentImage)
        }
    }
}