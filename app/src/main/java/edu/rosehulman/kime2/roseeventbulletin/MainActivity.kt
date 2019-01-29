package edu.rosehulman.kime2.roseeventbulletin

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ListFragment.OnEventSelectedListener {
    override fun onEventSelected(event: Event) {
        Log.d("EVENT_SELECTION", "Clicked on event")
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, EventDetailFragment.newInstance(event), getString(R.string.event_list_stack))
        ft.commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, EditEventFragment(), getString(R.string.event_list_stack))
            ft.commit()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var switchTo: Fragment? = null
        when (item.itemId) {
            R.id.nav_events -> {
                switchTo = ListFragment()
            }
            R.id.nav_map -> {

            }
            R.id.nav_profile-> {
                switchTo = ProfileFragment()
            }
        }
        if (switchTo != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo, getString(R.string.event_list_stack))
            ft.commit()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
