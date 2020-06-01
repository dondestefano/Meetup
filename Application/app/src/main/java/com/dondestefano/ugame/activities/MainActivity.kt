package com.dondestefano.ugame.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.dondestefano.ugame.R
import com.dondestefano.ugame.fragments.FriendsListFragment
import com.dondestefano.ugame.fragments.EventListFragment
import com.dondestefano.ugame.data_managers.UserDataManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header.view.*

const val EVENT_FRAGMENT = "event_fragment"
const val FRIENDS_FRAGMENT = "friend_fragment"

class ListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var eventListFragment: EventListFragment
    lateinit var friendsListFragment: FriendsListFragment
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        eventListFragment = EventListFragment()
        friendsListFragment = FriendsListFragment()

        // Show eventListFragment when the app starts.
        replaceFragment(eventListFragment, EVENT_FRAGMENT)

        setUpNavDrawer()
    }

    private fun setUpNavDrawer() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        setSupportActionBar(toolbar)
        navView.setBackgroundResource(R.color.colorNeutral)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        getLoggedInUser()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.nav_logout -> {
                logout()
            }

            R.id.nav_events -> {
                replaceFragment(eventListFragment, EVENT_FRAGMENT)
            }

            R.id.nav_friends -> {
                replaceFragment(friendsListFragment, FRIENDS_FRAGMENT)
            }

            R.id.nav_profile -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                val userID = UserDataManager.loggedInUser.userID
                intent.putExtra("USER_ID", userID)
                startActivity(intent)
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        eventListFragment.eventRecyclerView?.adapter?.notifyDataSetChanged()
    }

    // Hate this. Need it.
    // Gives the navDrawer enough time to setup before fetching the image.
    private fun getLoggedInUser() {
        val loggedInUserID = auth.currentUser?.uid
        UserDataManager.userDataRef = loggedInUserID?.let { UserDataManager.allUsersRef.document(it) }!!
        UserDataManager.userDataRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                UserDataManager.loggedInUser = snapshot.toObject(com.dondestefano.ugame.objects.User::class.java)!!

                navView.getHeaderView(0).nav_textview_username.text = UserDataManager.loggedInUser.name

                val uri = UserDataManager.loggedInUser.profileImageURL
                Picasso.get().load(uri).into(navView.getHeaderView(0).nav_profile_image)
            }
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent (this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, tag)
        transaction.commit()
    }
}
