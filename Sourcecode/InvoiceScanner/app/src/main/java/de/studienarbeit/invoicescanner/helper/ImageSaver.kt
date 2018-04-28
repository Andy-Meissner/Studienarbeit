/**
 * Created by andym on 11.04.2018.
 */

package de.studienarbeit.invoicescanner.helper


import android.media.Image
import android.util.Log
import de.studienarbeit.invoicescanner.fragments.CameraFragment

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Saves a JPEG [Image] into the specified [File].
 */
internal class ImageSaver(
        /**
         * The JPEG image
         */
        private val image: Image,

        /**
         * The file we save the image into.
         */
        private val file: File,

        private val context : CameraFragment
) : Runnable {

    override fun run() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file).apply {
                write(bytes)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        } finally {
            image.close()
            mListener.onImageSaved()
            output?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }


    companion object {
        /**
         * Tag for the [Log].
         */
        private val TAG = "ImageSaver"
    }

    private var mListener = context as onImageSavedListener

    interface onImageSavedListener
    {
        fun onImageSaved()
    }
}

