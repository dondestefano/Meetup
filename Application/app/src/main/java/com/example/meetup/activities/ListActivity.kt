package com.example.meetup.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.recycle_adapters.EventRecycleAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


class ListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var eventRecyclerView : RecyclerView? = null
    lateinit var auth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        setUpNavDrawer()
        setEventRecycleAdapters()
        setFabButtons()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.nav_logout -> {
                logout()
            }

            R.id.nav_events -> {
                eventRecyclerView?.adapter?.notifyDataSetChanged()
            }

            R.id.nav_friends -> {
                goToFriends()
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

    private fun setUpNavDrawer() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        navView.setBackgroundResource(R.color.colorNeutral)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        getLoggedInUser()
    }

    override fun onResume() {
        super.onResume()
        eventRecyclerView?.adapter?.notifyDataSetChanged()
    }

    // Hate this. Need it.
    private fun getLoggedInUser() {
        val loggedInUserID = auth.currentUser?.uid
        UserDataManager.userDataRef = loggedInUserID?.let { UserDataManager.allUsersRef.document(it) }!!
        UserDataManager.userDataRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                UserDataManager.loggedInUser = snapshot.toObject(com.example.meetup.objects.User::class.java)!!

                navView.getHeaderView(0).nav_textview_username.text = UserDataManager.loggedInUser.name

                val uri = UserDataManager.loggedInUser.profileImageURL
                Picasso.get().load(uri).into(navView.getHeaderView(0).nav_profile_image)
            }
        }
    }

    private fun setEventRecycleAdapters() {
        eventRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        eventRecyclerView?.layoutManager = LinearLayoutManager(this)

        val eventAdapter = EventRecycleAdapter(this)
        eventAdapter.updateItemsToList(EventDataManager.itemsList)
        eventRecyclerView?.adapter = eventAdapter

        eventRecyclerView?.let { EventDataManager.setFirebaseListener(it) }
    }

    private fun setFabButtons() {
        val fab = findViewById<View>(R.id.addEventActionButton)
        fab.setOnClickListener{
            val intent = Intent(this, AddAndEditEventActivity::class.java)
            intent.putExtra("EVENT_POSITION", "NO_LIST")
            startActivity(intent)
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent (this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToFriends() {
        val intent = Intent(this, FriendListActivity::class.java)
        startActivity(intent)
    }
}
