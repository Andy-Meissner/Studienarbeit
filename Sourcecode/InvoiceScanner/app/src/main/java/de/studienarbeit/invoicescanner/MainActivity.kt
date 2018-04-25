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
import de.studienarbeit.invoicescanner.fragments.*
import de.studienarbeit.invoicescanner.fragments.RecyclerViewFragment


class MainActivity : AppCompatActivity(), RetakeConfirmFragment.OnButtonClickedListener, CameraFragment.onImageTakenListener {
    override fun onImageSaved(path: String) {
        val imageAnalyer = ImageAnalyzer(this, path)
        imageAnalyer.analyse()
        currentInvoice = imageAnalyer.getInvoice()
    }

    private val cameraFragment : CameraFragment = CameraFragment.newInstance()
    private val archiveFragment = ArchiveFragment()
    private val favoritesFragment = FavoritesFragment()
    private val aboutFragment = AboutFragment()
    private val recyclerViewFragment = RecyclerViewFragment()
    private var currentFragment : Fragment? = null

    private lateinit var toolbar : Toolbar
    private var actionbar : ActionBar? = null

    private var hideIcon = true
    private var isMenuAvailable = true
    lateinit var currentInvoice : Invoice

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
        currentFragment = Fragment.CONFIRM_RETAKE
    }

    override fun onButtonAnalyze() {
        val fragment = PictureAnalyzedFragment()
        val args = Bundle()
        args.putString("imagepath", currentInvoice.imagePath)
        args.putString("text", currentInvoice.bic)
        fragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit()
        setFullscreenMode(false)
        actionbar!!.title = getString(R.string.new_invoice)
        hideIcon = false
        invalidateOptionsMenu()
        currentFragment = Fragment.ANALYZE_PICTURE
    }

    override fun onButtonDismiss() {
        supportFragmentManager.popBackStack()
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        currentFragment = Fragment.CAMERA
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

        val invoice = Invoice(null, "", "meine Iban", "", 0.0, "", "receiver", false)

        object : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void): Int? {
                for(i in 0 until 10){
                    db.invoiceDao().insertInvoice(invoice)
                }
                recyclerViewFragment.initDataset(db.invoiceDao().all)
                return 0
            }

            override fun onPostExecute(resultCode: Int?) {
            }
        }.execute()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionbar = supportActionBar
        actionbar!!.setDisplayShowTitleEnabled(false)
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        currentFragment = Fragment.CAMERA

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
                    currentFragment = Fragment.CAMERA}

                R.id.nav_archive ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, recyclerViewFragment).commit()
                    setFullscreenMode(false)
                    actionbar!!.setTitle(R.string.archive)
                    currentFragment = Fragment.ARCHIVE}

                R.id.nav_favorites ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, favoritesFragment).commit()
                    setFullscreenMode(false)
                    actionbar!!.setTitle(R.string.favorites)
                    currentFragment = Fragment.FAVORITES}

                R.id.nav_about ->
                    {supportFragmentManager.beginTransaction().replace(R.id.container, aboutFragment).commit()
                    setFullscreenMode(false)
                    actionbar!!.title = getString(R.string.about) + " " + getString(R.string.app_name)
                    currentFragment = Fragment.ABOUT}
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
                    if(currentFragment == Fragment.ANALYZE_PICTURE) {
                        setFullscreenMode(true)
                        hideIcon = true
                        invalidateOptionsMenu()
                        currentFragment = Fragment.CONFIRM_RETAKE
                    } else if (currentFragment == Fragment.CONFIRM_RETAKE) {
                        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
                        currentFragment = Fragment.CAMERA
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

    private fun onSaveButtonClicked()
    {

        object : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void): Int? {
                db.invoiceDao().insertInvoice(currentInvoice)
                return 0
            }

            override fun onPostExecute(resultCode: Int?) {
            }
        }.execute()
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
