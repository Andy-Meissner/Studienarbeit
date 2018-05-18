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
import android.arch.persistence.room.Room
import android.os.AsyncTask
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import de.studienarbeit.invoicescanner.fragments.*
import de.studienarbeit.invoicescanner.fragments.RecyclerViewFragment
import android.app.SearchManager
import android.content.Context
import android.widget.SearchView
import android.content.Intent
import android.widget.Toast
import android.app.Activity
import android.content.ContextWrapper
import android.provider.MediaStore
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_picture_analyzed.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), CameraFragment.onImageTakenListener, PictureAnalyzedFragment.OnImagedSavedListener, RecyclerViewFragment.OnInvoiceChangedListener {

    private val cameraFragment = CameraFragment.newInstance()
    private val archiveFragment = RecyclerViewFragment()
    private val favoritesFragment = RecyclerViewFragment()
    private val aboutFragment = AboutFragment()
    private val pictureAnalyzedFragment = PictureAnalyzedFragment()
    private val detailsFragment = PictureAnalyzedFragment()
    private var currentFragment : Fragment? = null
    private var previousFragment : Fragment? = null

    private lateinit var toolbar : Toolbar
    private var actionbar : ActionBar? = null
    private var new_invoice = true

    private var hideSearchButton = true
    private var hideEditButton = true
    private var hideAddImage = false
    private var hideSaveButton = true
    private var isMenuAvailable = true
    lateinit var currentInvoice : Invoice

    lateinit var db : AppDatabase

    override fun onInvoiceChanged(invoice: Invoice, action: String) {
        when(action)
        {
            "update" ->
                Thread(
                        Runnable {
                            db.invoiceDao().updateInvoice(invoice)
                            updateData()
                            runOnUiThread({
                            archiveFragment.updateRecyclerView()
                            favoritesFragment.updateRecyclerView()})
                        }
                ).start()

            "delete" ->
                Thread(
                        Runnable {
                            db.invoiceDao().deleteInvoice(invoice)
                            updateData()
                            runOnUiThread({
                            archiveFragment.updateRecyclerView()
                            favoritesFragment.updateRecyclerView()})
                        }
                ).start()
        }
    }

    /*
    Dont call from Main Thread
     */
    fun updateData()
    {
        var data = db.invoiceDao().all
        archiveFragment.initDataset(data)
        var favs = db.invoiceDao().favorites
        favoritesFragment.initDataset(favs)
    }


    override fun onImageSaved() {
        setFragment(archiveFragment)
    }

    override fun onImageAvailable(path: String) {
        new_invoice = true
        val args = Bundle()
        args.putString("imagepath", path)
        pictureAnalyzedFragment.arguments = args
        setFragment(pictureAnalyzedFragment)
        val imageAnalyzer = ImageAnalyzer(this, path)
        imageAnalyzer.analyse()
        currentInvoice = imageAnalyzer.getInvoice()
        pictureAnalyzedFragment.onImageAnalyzed(currentInvoice)
    }

    override fun openDetails(invoice: Invoice) {
        new_invoice = false
        val args = Bundle()
        args.putString("imagepath", invoice.imagePath)
        detailsFragment.arguments = args
        detailsFragment.actionBarTitle = TITLE_EDIT_INVOICE
        detailsFragment.isEditAvailable = true
        detailsFragment.isSaveAvailable = false
        setFragment(detailsFragment)
        detailsFragment.onImageAnalyzed(invoice)
    }

    private var mDrawerLayout : DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        archiveFragment.actionBarTitle = TITLE_ARCHIVE
        favoritesFragment.actionBarTitle = TITLE_FAVORITES

        setContentView(R.layout.activity_main)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .add(R.id.container, cameraFragment)
                .commit()

        db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "database-name").build()

        Thread(Runnable {
            archiveFragment.initDataset(db.invoiceDao().all)
            favoritesFragment.initDataset(db.invoiceDao().favorites)}
        ).start()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionbar = supportActionBar
        actionbar!!.setDisplayShowTitleEnabled(false)
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        currentFragment = cameraFragment

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
                    {
                        setFragment(cameraFragment)
                    }
                R.id.nav_archive ->
                    {
                        setFragment(archiveFragment)
                        Thread(
                                Runnable {
                                    updateData()
                                }
                                ).start()

                    }
                R.id.nav_favorites ->
                    {
                        setFragment(favoritesFragment)
                        Thread(
                                Runnable {
                                    updateData()
                                }
                                ).start()
                    }
                R.id.nav_about ->
                    {setFragment(aboutFragment)}
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
        menuInflater.inflate(R.menu.options_menu, menu)

        menu!!.findItem(R.id.search).isVisible = !hideSearchButton
        menu.findItem(R.id.edit).isVisible = !hideEditButton
        menu.findItem(R.id.add_photo).isVisible = !hideAddImage
        menu.findItem(R.id.save_button).isVisible = !hideSaveButton

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(isMenuAvailable){
                    mDrawerLayout!!.openDrawer(GravityCompat.START)
                } else {
                    supportFragmentManager.popBackStack()
                    setFragment(previousFragment!!, true)
                }
                return true
            }
            R.id.save_button -> {
                onSaveButtonClicked()
                return true
            }
            R.id.add_photo -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        var cw = ContextWrapper(applicationContext)
                        var fileDir = cw.getDir("profile", Context.MODE_PRIVATE)
                        if(!fileDir.exists())
                        {
                            fileDir.mkdir()
                        }
                        var myfile = File(fileDir,"temp.jpg")
                        var fos : FileOutputStream? = null
                        try{
                            fos = FileOutputStream(myfile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
                            fos.close()
                        } catch (e : Exception)
                        {
                            Log.w("SAVE_IMAGE", e.message, e)
                        }
                        onImageAvailable(myfile.absolutePath)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setFragment(previousFragment!!, true)
    }

    private fun onSaveButtonClicked()
    {
        var myinv = pictureAnalyzedFragment.getInvoice()
        object : AsyncTask<Void, Void, Int>() {
            override fun doInBackground(vararg params: Void): Int? {
                if (new_invoice)
                {
                    db.invoiceDao().insertInvoice(myinv)
                }
                else
                {
                    db.invoiceDao().updateInvoice(myinv)
                }
                updateData()
                runOnUiThread({archiveFragment.updateRecyclerView()})
                return 0
            }

            override fun onPostExecute(resultCode: Int?) {
            }
        }.execute()

        pictureAnalyzedFragment.saveImage()
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

    private fun setFragment(fragment : Fragment, backmode : Boolean = false) {
        if(currentFragment != fragment) {
            runOnUiThread {
                if(!backmode) {
                    if (currentFragment == cameraFragment ||
                            fragment == detailsFragment) {
                        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit()
                        previousFragment = currentFragment
                    } else {
                        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                    }
                }
                currentFragment = fragment
                fragment as FragmentAttributeInterface
                setFullscreenMode(fragment.fullScreen)
                actionbar!!.title = fragment.actionBarTitle

                isMenuAvailable = fragment.isMenuAvailable
                if(isMenuAvailable) {
                    actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
                } else {
                    actionbar!!.setHomeAsUpIndicator(R.drawable.ic_menu_back)
                }


                hideAddImage = !fragment.isAddImageAvailable
                hideSaveButton = !fragment.isSaveAvailable
                hideEditButton = !fragment.isEditAvailable
                invalidateOptionsMenu()
            }
        }
    }
}
