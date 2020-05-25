package com.example.meetup.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.R
import com.example.meetup.recycle_adapters.FriendRecycleAdapter
import com.google.firebase.auth.FirebaseAuth

class FriendListActivity : AppCompatActivity() {
    private var friendRecyclerView : RecyclerView? = null
    private lateinit var toolbar: Toolbar
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        auth = FirebaseAuth.getInstance()

        FriendDataManager.resetFriendDataManagerUser()
        setFriendRecycleAdapters()
        setFabButtons()
        setupToolbar()
        friendRecyclerView?.let { FriendDataManager.setFirebaseListenerForFriends(it) }
    }

    private fun setFriendRecycleAdapters() {
        friendRecyclerView = findViewById<RecyclerView>(R.id.friendRecyclerView)
        friendRecyclerView?.layoutManager = LinearLayoutManager(this)

        val friendAdapter = FriendRecycleAdapter(this)
        friendAdapter.updateItemsToList(FriendDataManager.itemsList)
        friendRecyclerView?.adapter = friendAdapter
    }

    private fun setupToolbar() {
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarFriends)
        setSupportActionBar(toolbar)

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    private fun setFabButtons() {
        val fab = findViewById<View>(R.id.addFriendActionButton)
        fab.setOnClickListener { view ->
            val intent = Intent(this, SearchUsersActivity::class.java)
            startActivity(intent)
        }
    }
}