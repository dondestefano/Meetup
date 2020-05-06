package com.example.meetup.Activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.DataManagers.EventDataManager
import com.example.meetup.DataManagers.UserDataManager
import com.example.meetup.Objects.Event
import com.example.meetup.Objects.User
import com.example.meetup.R
import com.example.meetup.RecycleAdapters.UserRecycleAdapter

const val EVENT_EXTRA = "EVENT"

class InviteActivity : AppCompatActivity() {
    private var userRecyclerView : RecyclerView? = null
    private lateinit var searchField : EditText
    private lateinit var inviteButton : Button
    private lateinit var event : Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        searchField = findViewById(R.id.searchUserEditText)
        inviteButton = findViewById(R.id.inviteButton)

        getExtraFromIntent()
        setOnclickListeners()

        setEventRecycleAdapter()
        userRecyclerView?.let { UserDataManager.setFirebaseListenerForUsers(it) }
    }

    fun setOnclickListeners() {
        inviteButton.setOnClickListener {
            event?.keyName?.let { EventDataManager.inviteUserToEvent(it, event) }
            println("!!! ${event.keyName.toString()}")
            finish()
        }
    }


    private fun setEventRecycleAdapter() {
        userRecyclerView = findViewById<RecyclerView>(R.id.userInviteRecycleView)
        userRecyclerView?.layoutManager = LinearLayoutManager(this)

        val userAdapter = UserRecycleAdapter(this)
        userRecyclerView?.adapter = userAdapter
        userAdapter.updateItemsToList(UserDataManager.allUsersList)
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        event = intent.getSerializableExtra(EVENT_EXTRA) as Event
    }


}
