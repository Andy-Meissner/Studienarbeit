package de.studienarbeit.invoicescanner

import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class NotFullscreenActivity : AppCompatActivity() {

    private var mDrawerLayout : DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_not_fullscreen)

        val menuItemId = intent.getIntExtra(MENU_ITEM_ID, R.id.nav_archive)
        var fragment : Fragment

        when(menuItemId){
            R.id.nav_archive -> fragment = ArchiveFragment()
            R.id.nav_favorites -> fragment = FavoritesFragment()
            R.id.nav_about -> fragment = AboutFragment()
            else -> fragment = ArchiveFragment()
        }

        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
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
                R.id.nav_camera -> startActivity(Intent(this,MainActivity::class.java))
                R.id.nav_archive -> supportFragmentManager.beginTransaction().replace(R.id.container, ArchiveFragment()).commit()
                R.id.nav_favorites -> supportFragmentManager.beginTransaction().replace(R.id.container, FavoritesFragment()).commit()
                R.id.nav_about -> supportFragmentManager.beginTransaction().replace(R.id.container, AboutFragment()).commit()
            }

            true
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