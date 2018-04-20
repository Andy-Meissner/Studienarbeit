package de.studienarbeit.invoicescanner


import android.content.Intent
import android.app.Fragment
import android.app.FragmentTransaction
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast


const val MENU_ITEM_ID = "de.studienarbeit.invoicescanner.MENU_ITEM"


class MainActivity : AppCompatActivity(), RetakeConfirmFragment.onButtonClickedListener, CameraFragment.onImageTakenListener {
    override fun onImageTaken() {
        val fragment = RetakeConfirmFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
    }


    override fun onButtonAnalyze() {
        val text = "Hello toast!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    override fun onButtonDismiss() {
        val text = "Hello toast!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    private var mDrawerLayout : DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val camFrag = CameraFragment.newInstance()
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .add(R.id.container, camFrag)
                .commit()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayShowTitleEnabled(false)
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white)


        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout!!.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            when(menuItem.itemId){
                R.id.nav_archive, R.id.nav_favorites, R.id.nav_about -> startActivity(Intent(this,NotFullscreenActivity::class.java).apply{
                    putExtra(MENU_ITEM_ID, menuItem.itemId)
                })
            }

            true
        }


        // MACHE WOANDERS
        val display = windowManager.defaultDisplay
        val resolution = Point()
        display.getRealSize(resolution)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            camFrag.windowResolution = Point(resolution.y,resolution.x)
        }
        else
        {
            camFrag.windowResolution = resolution
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
