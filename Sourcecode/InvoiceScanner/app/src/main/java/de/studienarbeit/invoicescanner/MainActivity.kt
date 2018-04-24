package de.studienarbeit.invoicescanner


import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import java.io.File
import android.arch.persistence.room.Room
import android.os.AsyncTask
import android.support.v7.app.ActionBar

enum class Fragments {
    CAMERA,
    ARCHIVE,
    FAVORITES,
    ABOUT,
    CONFIRM_RETAKE,
    ANALYZE_PICTURE
}

class MainActivity : AppCompatActivity(), RetakeConfirmFragment.OnButtonClickedListener, CameraFragment.onImageTakenListener {

    private val cameraFragment : CameraFragment = CameraFragment.newInstance()
    private val archiveFragment = ArchiveFragment()
    private val favoritesFragment = FavoritesFragment()
    private val aboutFragment = AboutFragment()
    private var currentFragment : Fragments? = null

    private lateinit var toolbar : Toolbar
    private var actionbar : ActionBar? = null

    private var hideIcon = true
    private var isMenuAvailable = true

    lateinit var db : AppDatabase

    override fun onImageTaken(file : File) {
        val fragment = RetakeConfirmFragment()
        val args = Bundle()
        args.putString("imagepath",file.absolutePath)
        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit()
        runOnUiThread {
            actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_back)
        }
        isMenuAvailable = false
        currentFragment = Fragments.CONFIRM_RETAKE
    }

    override fun onButtonAnalyze(invoice: Invoice) {

        object : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void): Int? {
                db.invoiceDao().insertInvoice(invoice)
                return 0
            }

            override fun onPostExecute(resultCode: Int?) {
            }
        }.execute()

        val fragment = PictureAnalyzedFragment()
        val args = Bundle()
        args.putString("imagepath", invoice.imagePath)
        args.putString("text", invoice.bic)
        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit()
        setFullscreenMode(false)
        actionbar!!.title = getString(R.string.new_invoice)
        hideIcon = false
        invalidateOptionsMenu()
        currentFragment = Fragments.ANALYZE_PICTURE
    }

    override fun onButtonDismiss() {
        supportFragmentManager.popBackStack()
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        currentFragment = Fragments.CAMERA
    }

    private var mDrawerLayout : DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .add(R.id.container, cameraFragment)
                .commit()

        db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "database-name").build()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionbar = supportActionBar
        actionbar!!.setDisplayShowTitleEnabled(false)
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        currentFragment = Fragments.CAMERA

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
                R.id.nav_camera ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, cameraFragment).commit()
                    setFullscreenMode(true)
                    currentFragment = Fragments.CAMERA}

                R.id.nav_archive ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, archiveFragment).commit()
                    setFullscreenMode(false)
                    actionbar!!.setTitle(R.string.archive)
                    currentFragment = Fragments.ARCHIVE}

                R.id.nav_favorites ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, favoritesFragment).commit()
                    setFullscreenMode(false)
                    actionbar!!.setTitle(R.string.favorites)
                    currentFragment = Fragments.FAVORITES}

                R.id.nav_about ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, aboutFragment).commit()
                    setFullscreenMode(false)
                    actionbar!!.title = getString(R.string.about) + " " + getString(R.string.app_name)
                    currentFragment = Fragments.ABOUT}
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
            cameraFragment.windowResolution = Point(resolution.y,resolution.x)
        }
        else
        {
            cameraFragment.windowResolution = resolution
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        menu!!.findItem(R.id.save_button).isVisible = !hideIcon
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(isMenuAvailable){
                    mDrawerLayout!!.openDrawer(GravityCompat.START)
                } else {
                    supportFragmentManager.popBackStack()
                    if(currentFragment == Fragments.ANALYZE_PICTURE) {
                        setFullscreenMode(true)
                        hideIcon = true
                        invalidateOptionsMenu()
                        currentFragment = Fragments.CONFIRM_RETAKE
                    } else if (currentFragment == Fragments.CONFIRM_RETAKE) {
                        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
                        currentFragment = Fragments.CAMERA
                        isMenuAvailable = true
                    }
                }
                return true
            }
            R.id.save_button -> {
                hideIcon = true
                invalidateOptionsMenu()
                Toast.makeText(applicationContext, "Invoice saved", Toast.LENGTH_LONG).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setFullscreenMode(yes : Boolean) {
        if(yes) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            actionbar!!.setDisplayShowTitleEnabled(false)
            toolbar.setBackgroundResource(R.color.transparent)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            actionbar!!.setDisplayShowTitleEnabled(true)
            toolbar.setBackgroundResource(R.color.colorPrimaryDark)
        }
    }

}
