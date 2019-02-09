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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ListFragment.OnEventSelectedListener, SplashFragment.OnLoginButtonPressedListener {

    val auth = FirebaseAuth.getInstance()
    lateinit var authListener: FirebaseAuth.AuthStateListener
    private val RC_ROSEFIRE_LOGIN = 1
    private var loggedInUser: FirebaseUser? = null
    private val userRef = FirebaseFirestore.getInstance().collection("users")

    fun onRosefireLogin() {
        val signInIntent = Rosefire.getSignInIntent(this, getString(R.string.token))
        startActivityForResult(signInIntent, RC_ROSEFIRE_LOGIN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_ROSEFIRE_LOGIN) {
            val result = Rosefire.getSignInResultFromIntent(data)
            if (result.isSuccessful) {
                auth.signInWithCustomToken(result.token)
            } else {
                // Failed
            }
        }
    }

    override fun onEventSelected(event: Event) {
        Log.d("EVENT_SELECTION", "Clicked on event")
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, EventDetailFragment.newInstance(event), getString(R.string.event_list_stack))
        ft.addToBackStack("list")
        ft.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, EditEventFragment(), getString(R.string.event_list_stack))
            ft.addToBackStack("create")
            ft.commit()
        }

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
            val user = auth.currentUser
            loggedInUser = user
            if (user != null) {
                Log.d("USER", "UID: ${user.uid}")
                // TODO: Fix this check. Need to use Firebase id instead of RoseFire UID
                userRef.document(user.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        Log.d("USER", it.toString())
                    } else {
                        Log.d("USER", "First time logging in: ${user.uid}")
                        userRef.add(user)
                    }
                }

                switchToListFragment(user.uid)
            } else {
                switchToSplashFragment()
            }
        }
    }

    private fun switchToSplashFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, SplashFragment())
        ft.commit()
    }

    private fun switchToListFragment(uid: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ListFragment.newInstance(uid), getString(R.string.event_list_stack))
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        ft.commit()
    }
    private fun switchToProfileFragment(uid: String) {
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
                if (loggedInUser != null) {
                    switchToListFragment(loggedInUser!!.uid)
                } else {
                    switchToSplashFragment()
                }
            }
            R.id.nav_map -> {

            }
            R.id.nav_profile-> {
                switchToProfileFragment(loggedInUser!!.uid)
            }
            R.id.logout-> {
                auth.signOut()
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
