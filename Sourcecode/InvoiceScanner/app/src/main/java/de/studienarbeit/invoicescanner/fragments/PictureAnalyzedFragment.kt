package de.studienarbeit.invoicescanner.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import de.studienarbeit.invoicescanner.R
import de.studienarbeit.invoicescanner.REQUEST_CAMERA_PERMISSION
import de.studienarbeit.invoicescanner.REQUEST_EXTERNAL_FILE_STORAGE
import de.studienarbeit.invoicescanner.TITLE_NEW_INVOICE
import de.studienarbeit.invoicescanner.helper.ConfirmationDialog
import de.studienarbeit.invoicescanner.helper.ErrorDialog
import java.io.File

class PictureAnalyzedFragment : Fragment(), FragmentAttributeInterface , ActivityCompat.OnRequestPermissionsResultCallback {

    override var isMenuAvailable = false
    override var actionBarTitle = TITLE_NEW_INVOICE
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

    fun askPermission()
    {
        val permission = ContextCompat.checkSelfPermission(this!!.activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            requestCameraPermission()
        }
    }
    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ConfirmationDialog().show(childFragmentManager, "dialog")
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_FILE_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_EXTERNAL_FILE_STORAGE) {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(childFragmentManager, "dialog")
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}