package de.studienarbeit.invoicescanner

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import android.hardware.camera2.*
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.support.v4.content.ContextCompat
import android.util.Log
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    val CAMERA_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}

class CameraPreview  : SurfaceView , SurfaceHolder.Callback{

    val mHolder : SurfaceHolder? = null
    val mCamera : CameraDevice? = null
    private val cameraOpenCloseLock = Semaphore(1)

    constructor(ctx: Context) : super(ctx){

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun checkCameraHardware(ctx : Context): Boolean {
        return ctx.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }
}