package de.studienarbeit.invoicescanner.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import de.studienarbeit.invoicescanner.*
import de.studienarbeit.invoicescanner.helper.ConfirmationDialog
import de.studienarbeit.invoicescanner.helper.ErrorDialog
import de.studienarbeit.invoicescanner.helper.showToast
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PictureAnalyzedFragment : Fragment(), FragmentAttributeInterface , ActivityCompat.OnRequestPermissionsResultCallback {

    override var isMenuAvailable = false
    override var actionBarTitle = TITLE_NEW_INVOICE
    override var fullScreen = false

    private lateinit var myListener : onImagedSavedListener
    private var viewCreated = false
    private lateinit var currentImage : Bitmap
    private lateinit var currentInvoice : Invoice
    private var dataAvailable = false

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
        viewCreated = true

        if (dataAvailable)
        {
            view!!.findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
            fillEditTextFields(currentInvoice)
        }

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            myListener = context as onImagedSavedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnArticleSelectedListener")
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_FILE_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_EXTERNAL_FILE_STORAGE) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                activity!!.showToast(getText(R.string.request_permission_ext_save).toString())
            }
            else
            {
                saveImageToExternalStorage(currentImage)
                myListener.onImageSaved()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun saveImage()
    {
        val permission = ContextCompat.checkSelfPermission(this!!.activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            requestCameraPermission()
            return
        }
        else
        {
            saveImageToExternalStorage(currentImage)
            myListener.onImageSaved()
        }

    }

    interface onImagedSavedListener
    {
        fun onImageSaved()
    }

    fun onImageAnalyzed(invoice : Invoice)
    {
        if (viewCreated) {
            view!!.findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
            fillEditTextFields(invoice)
        }
        dataAvailable = true
        currentInvoice = invoice
    }

    fun fillEditTextFields(inv : Invoice)
    {
        view!!.findViewById<EditText>(R.id.edit_iban).setText(inv.iban, TextView.BufferType.EDITABLE)
        view!!.findViewById<EditText>(R.id.edit_bic).setText(inv.bic, TextView.BufferType.EDITABLE)
        view!!.findViewById<EditText>(R.id.edit_amount).setText((inv.amount).toString(), TextView.BufferType.EDITABLE)
        view!!.findViewById<EditText>(R.id.edit_receiver).setText(inv.receiver, TextView.BufferType.EDITABLE)
        view!!.findViewById<EditText>(R.id.edit_details).setText(inv.details, TextView.BufferType.EDITABLE)

    }

    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val imagepath = ""
        val file = File(imagepath)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null,
                object : MediaScannerConnection.OnScanCompletedListener {
                    override fun onScanCompleted(path: String, uri: Uri) {
                        Log.i("ExternalStorage", "Scanned $path:")
                        Log.i("ExternalStorage", "-> uri=$uri")
                    }
                })

    }
}