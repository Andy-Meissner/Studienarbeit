package de.studienarbeit.invoicescanner


import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val camFrag = CameraFragment.newInstance()
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, camFrag)
                .commit()
        val display = windowManager.defaultDisplay
        val resolution : Point = Point()
        display.getSize(resolution)

        val orientation = getResources().getConfiguration().orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            camFrag.windowResolution = Point(resolution.y,resolution.x)
        }
        else
        {
            camFrag.windowResolution = resolution
        }

    }

}
