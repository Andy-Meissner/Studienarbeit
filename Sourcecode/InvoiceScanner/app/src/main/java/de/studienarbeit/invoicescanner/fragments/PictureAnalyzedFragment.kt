package de.studienarbeit.invoicescanner.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.media.MediaScannerConnection
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import de.studienarbeit.invoicescanner.*
import de.studienarbeit.invoicescanner.helper.showToast
import java.io.File
import java.io.FileOutputStream

@Suppress("NAME_SHADOWING")
class PictureAnalyzedFragment : Fragment(), FragmentAttributeInterface , ActivityCompat.OnRequestPermissionsResultCallback {

    override var isMenuAvailable = false
    override var isAddImageAvailable = false
    override var isSaveAvailable = true
    override var isEditAvailable = false
    override var actionBarTitle = TITLE_NEW_INVOICE
    override var fullScreen = false

    private lateinit var myListener : OnImagedSavedListener
    private lateinit var currentImage : Bitmap
    private lateinit var currentInvoice : Invoice
    private var dataAvailable = false
    var mCurrentAnimator : Animator? = null
    private var mShortAnimationDuration = 500

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View? = inflater.inflate(R.layout.fragment_picture_analyzed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagePath = arguments!!.get("imagepath") as String

        val imgFile = File(imagePath)

        if (imgFile.exists()) {

            currentImage = BitmapFactory.decodeFile(imgFile.absolutePath)
            val imageView = view.findViewById<ImageView>(R.id.captured_image)
            imageView.setImageBitmap(currentImage)
            imageView.setOnClickListener({
                zoomImageFromThumb(imageView,currentImage)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (dataAvailable)
        {
            fillEditTextFields(currentInvoice)
        }

        if (actionBarTitle == TITLE_NEW_INVOICE)
        {
            switchToEditMode()
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            myListener = context as OnImagedSavedListener
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
        val permission = ContextCompat.checkSelfPermission(this.activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    fun getInvoice(): Invoice {
        if (view != null)
        {
            currentInvoice.iban = view!!.findViewById<EditText>(R.id.edit_iban).text.toString()
            currentInvoice.bic = view!!.findViewById<EditText>(R.id.edit_bic).text.toString()
            currentInvoice.amount = view!!.findViewById<EditText>(R.id.edit_amount).text.toString().toDouble()
            currentInvoice.receiver = view!!.findViewById<EditText>(R.id.edit_receiver).text.toString()
            currentInvoice.details = view!!.findViewById<EditText>(R.id.edit_details).text.toString()
        }

        return currentInvoice

    }

    interface OnImagedSavedListener
    {
        fun onImageSaved()
    }

    fun onImageAnalyzed(invoice : Invoice)
    {
        fillEditTextFields(invoice)
        currentInvoice = invoice
        dataAvailable = true
    }

    private fun fillEditTextFields(inv : Invoice)
    {
        if (view != null)
        {
            view!!.findViewById<EditText>(R.id.edit_iban).setText(inv.iban, TextView.BufferType.EDITABLE)
            view!!.findViewById<EditText>(R.id.edit_bic).setText(inv.bic, TextView.BufferType.EDITABLE)
            view!!.findViewById<EditText>(R.id.edit_amount).setText((inv.amount).toString(), TextView.BufferType.EDITABLE)
            view!!.findViewById<EditText>(R.id.edit_receiver).setText(inv.receiver, TextView.BufferType.EDITABLE)
            view!!.findViewById<EditText>(R.id.edit_details).setText(inv.details, TextView.BufferType.EDITABLE)

            view!!.findViewById<TextView>(R.id.iban_textview).text = inv.iban
            view!!.findViewById<TextView>(R.id.bic_textview).text = inv.bic
            view!!.findViewById<TextView>(R.id.amount_textview).text = inv.amount.toString()
            view!!.findViewById<TextView>(R.id.receiver_textview).text = inv.receiver
            view!!.findViewById<TextView>(R.id.details_textview).text = inv.details
        }
    }

    fun switchToEditMode()
    {
        if (view != null)
        {
            view!!.findViewById<ViewSwitcher>(R.id.iban_switch).showNext()
            view!!.findViewById<ViewSwitcher>(R.id.bic_switch).showNext()
            view!!.findViewById<ViewSwitcher>(R.id.amount_switch).showNext()
            view!!.findViewById<ViewSwitcher>(R.id.receiver_switch).showNext()
            view!!.findViewById<ViewSwitcher>(R.id.details_switch).showNext()
        }
    }

    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val imagepath = currentInvoice.imagePath
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
        MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }

    }

    private fun zoomImageFromThumb(thumbView: View, bmp: Bitmap) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator!!.cancel()
        }

        // Load the high-resolution "zoomed-in" image.
        val expandedImageView = view!!.findViewById<ImageView>(R.id.expanded_image)
        expandedImageView.setImageBitmap(bmp)

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBounds = Rect()
        val finalBounds = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds)
        view!!.findViewById<ConstraintLayout>(R.id.invoice_details_container)
                .getGlobalVisibleRect(finalBounds, globalOffset)
        startBounds.offset(-globalOffset.x, -globalOffset.y)
        finalBounds.offset(-globalOffset.x, -globalOffset.y)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float
        if ((finalBounds.width()).div(finalBounds.height()) > startBounds.width().div(startBounds.height())) {
            // Extend start bounds horizontally
            startScale = (startBounds.height() / finalBounds.height()).toFloat()
            val startWidth = startScale * finalBounds.width()
            val deltaWidth = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width().toFloat() / finalBounds.width()
            val startHeight = startScale * finalBounds.height()
            val deltaHeight = (startHeight - startBounds.height().toFloat()) / 2
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f
        expandedImageView.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.pivotX = 0f
        expandedImageView.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        val set = AnimatorSet()
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left.toFloat(), finalBounds.left.toFloat()))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top.toFloat(), finalBounds.top.toFloat()))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f))
        set.duration = mShortAnimationDuration.toLong()
        set.interpolator = DecelerateInterpolator()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mCurrentAnimator = null
            }

            override fun onAnimationCancel(animation: Animator) {
                mCurrentAnimator = null
            }
        })
        set.start()
        mCurrentAnimator = set

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        expandedImageView.setOnClickListener({
            if (mCurrentAnimator != null) {
                mCurrentAnimator!!.cancel()
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            val set = AnimatorSet()
            set.play(ObjectAnimator
                    .ofFloat(expandedImageView, View.X, startBounds.left.toFloat()))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.Y, startBounds.top.toFloat()))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_X, startScale))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_Y, startScale))
            set.duration = mShortAnimationDuration.toLong()
            set.interpolator = DecelerateInterpolator()
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    thumbView.alpha = 1f
                    expandedImageView.visibility = View.GONE
                    mCurrentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    thumbView.alpha = 1f
                    expandedImageView.visibility = View.GONE
                    mCurrentAnimator = null
                }
            })
            set.start()
            mCurrentAnimator = set
        })
    }
}