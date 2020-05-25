package com.example.meetup.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R
import com.example.meetup.recycle_adapters.AllUsersRecycleAdapter
import com.google.firebase.auth.FirebaseAuth

class SearchUsersActivity : AppCompatActivity() {
    private var searchUsersRecyclerView : RecyclerView? = null
    private lateinit var toolbar: Toolbar
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

        auth = FirebaseAuth.getInstance()

        setSearchUserRecycleAdapter()
        searchUsersRecyclerView?.let { UserDataManager.setFirebaseListenerForUsers(it) }
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarSearchFriends)
        setSupportActionBar(toolbar)

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    private fun setSearchUserRecycleAdapter() {
        searchUsersRecyclerView = findViewById<RecyclerView>(R.id.allUsersRecyclerView)
        searchUsersRecyclerView?.layoutManager = LinearLayoutManager(this)

        val allUsersAdapter =
            AllUsersRecycleAdapter(this)
        allUsersAdapter.updateItemsToList(UserDataManager.allUsersList)
        searchUsersRecyclerView?.adapter = allUsersAdapter

    }
}
