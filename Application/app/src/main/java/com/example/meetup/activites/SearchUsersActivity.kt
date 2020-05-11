package com.example.meetup.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R
import com.example.meetup.recycle_adapters.SearchUserRecycleAdapter
import com.google.firebase.auth.FirebaseAuth

class SearchUsersActivity : AppCompatActivity() {
    private var searchUsersRecyclerView : RecyclerView? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

        auth = FirebaseAuth.getInstance()

        setSearchUserRecycleAdapter()
        searchUsersRecyclerView?.let { UserDataManager.setFirebaseListenerForUsers(it) }
    }

    private fun setSearchUserRecycleAdapter() {
        println("!!! Doing it")
        searchUsersRecyclerView = findViewById<RecyclerView>(R.id.allUsersRecyclerView)
        searchUsersRecyclerView?.layoutManager = LinearLayoutManager(this)

        val allUsersAdapter =
            SearchUserRecycleAdapter(this)
        allUsersAdapter.updateItemsToList(UserDataManager.allUsersList)
        searchUsersRecyclerView?.adapter = allUsersAdapter

    }
}
