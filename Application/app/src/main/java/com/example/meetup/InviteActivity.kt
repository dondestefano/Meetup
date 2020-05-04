package com.example.meetup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InviteActivity : AppCompatActivity() {
    private var userRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        setEventRecycleAdapter()
        userRecyclerView?.let { UserDataManager.setFirebaseListenerForUsers(it) }
    }


    private fun setEventRecycleAdapter() {
        userRecyclerView = findViewById<RecyclerView>(R.id.userInviteRecycleView)
        userRecyclerView?.layoutManager = LinearLayoutManager(this)


        val userAdapter = UserRecycleAdapter(this)
        userRecyclerView?.adapter = userAdapter
        userAdapter.updateItemsToList(UserDataManager.allUsersList)
    }
}
