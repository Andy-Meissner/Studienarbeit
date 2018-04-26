package de.studienarbeit.invoicescanner.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import de.studienarbeit.invoicescanner.R
import java.io.File

class PictureAnalyzedFragment : Fragment(), FragmentAttributeInterface {

    override var isMenuAvailable = false
    override var actionBarTitle = ""
    override var fullScreen = false

    lateinit var currentImage : Bitmap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_picture_analyzed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagePath = arguments!!.get("imagepath") as String

        val imgFile = File(imagePath)

        if (imgFile.exists()) {

            currentImage = BitmapFactory.decodeFile(imgFile.absolutePath)

            view.findViewById<ImageView>(R.id.captured_image).setImageBitmap(currentImage)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        actionBarTitle = getString(R.string.new_invoice)
    }
}