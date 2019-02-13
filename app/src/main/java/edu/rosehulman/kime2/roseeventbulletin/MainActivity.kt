package edu.rosehulman.kime2.roseeventbulletin

import android.app.Activity
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.rosefire.Rosefire
import android.content.Intent
import android.support.v4.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.nav_header_main.view.*
import layout.Constants

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ListFragment.OnEventSelectedListener, SplashFragment.OnLoginButtonPressedListener {

    val dataService = DataService()
    val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val RC_ROSEFIRE_LOGIN = 1
    private var loggedInUser: String = ""
    private val userRef = FirebaseFirestore.getInstance().collection("users")
    private var menu: Menu? = null

    private fun onRosefireLogin() {
        val signInIntent = Rosefire.getSignInIntent(this, getString(R.string.token))
        startActivityForResult(signInIntent, RC_ROSEFIRE_LOGIN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(Constants.USER_LOG, "FAILED")

        if (resultCode == Activity.RESULT_OK && requestCode == RC_ROSEFIRE_LOGIN) {
            val result = Rosefire.getSignInResultFromIntent(data)
            if (result.isSuccessful) {
                auth.signInWithCustomToken(result.token)
                Log.d(Constants.USER_LOG, "Username: ${result.username}")
                Log.d(Constants.USER_LOG, "Name: ${result.name}")
                Log.d(Constants.USER_LOG, "Email: ${result.email}")
                Log.d(Constants.USER_LOG, "Group: ${result.group}")
                userRef
                    .whereEqualTo("username", result.username)
                    .get()
                    .addOnSuccessListener {
                    if (!it.isEmpty) {
                        Log.d(Constants.USER_LOG, "User exists: ${it.documents}")
                        loggedInUser = User.fromSnapShot(it.documents[0]).id
                    } else {
                        Log.d(Constants.USER_LOG, "First time logging in: ${result.username}")
                        loggedInUser = result.username
                        val user = User(result.username, "", result.name, result.email)
                        userRef.add(user)
                    }
                }
            } else {
                // Failed
                Log.d(Constants.USER_LOG, "FAILED")
            }
        }
    }

    override fun onEventSelected(event: Event) {
        Log.d("EVENT_SELECTION", "Clicked on event")
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, EventDetailFragment.newInstance(event, loggedInUser), getString(R.string.event_list_stack))
        ft.addToBackStack("list")
        ft.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, EditEventFragment.newInstance(loggedInUser), getString(R.string.event_list_stack))
            ft.addToBackStack("create")
            ft.commit()
        }
        fab.hide()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        initializeListener()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun initializeListener() {
        authListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                Log.d(Constants.USER_LOG, auth.currentUser!!.uid)
                dataService.getUserIDByUsername(auth.currentUser!!.uid).addOnSuccessListener {
                    val uid = it.documents[0].id
                    loggedInUser = uid
                    switchToListFragment(loggedInUser)
                }
            } else {
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                switchToSplashFragment()
            }
        }
    }

    private fun switchToSplashFragment() {
        if (this.menu != null) {
            setMenuVisible(false)
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }



    private fun switchToMapFragment() {
        if (this.menu != null) {
            setMenuVisible(false)
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, MapsActivity())
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        ft.commit()
    }

    private fun switchToListFragment(uid: String) {
        if (this.menu != null) {
            setMenuVisible(false)
        }
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        dataService.getUserByUid(uid!!).addOnSuccessListener {
            drawer_layout.drawer_name.text = it.getString("name")
            drawer_layout.drawer_email.text = it.getString("email")
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ListFragment.newInstance(uid), getString(R.string.event_list_stack))
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        ft.commit()
    }
    private fun switchToProfileFragment(uid: String) {
        if (this.menu != null) {
            setMenuVisible(false)
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ProfileFragment.newInstance(uid), getString(R.string.event_list_stack))
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        ft.commit()
    }

    override fun onLoginButtonPressed() {
        onRosefireLogin()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (this.menu != null) {
                setMenuVisible(false)
            }
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_edit -> {
                false
            }
            R.id.action_delete -> {
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setMenuVisible(boolean: Boolean) {
        menu!!.findItem(R.id.action_edit).isVisible = boolean
        menu!!.findItem(R.id.action_delete).isVisible = boolean
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var switchTo: Fragment? = null
        when (item.itemId) {
            R.id.nav_events -> {
                if (auth.currentUser != null) {
                    switchToListFragment(loggedInUser)
                } else {
                    switchToSplashFragment()
                }
            }
            R.id.nav_map -> {
                switchToMapFragment()
            }
            R.id.nav_profile-> {
                switchToProfileFragment(loggedInUser)
            }
            R.id.logout-> {
                auth.signOut()
                fab.hide()
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        if (switchTo != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, switchTo, getString(R.string.event_list_stack))
            while (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
