package com.example.meetup.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.R
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.objects.Event
import com.example.meetup.recycle_adapters.InviteRecycleAdapter

const val EVENT_EXTRA = "EVENT"

class InviteActivity : AppCompatActivity() {
    private var userRecyclerView : RecyclerView? = null
    private lateinit var searchField : EditText
    private lateinit var inviteButton : Button
    private lateinit var event : Event
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        searchField = findViewById(R.id.searchUserEditText)
        inviteButton = findViewById(R.id.inviteButton)

        getExtraFromIntent()
        setOnclickListeners()
        setupToolbar()

        setInviteUserRecycleAdapter()


    }

    private fun setupToolbar() {
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarInvite)
        setSupportActionBar(toolbar)

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    private fun setOnclickListeners() {
        inviteButton.setOnClickListener {
            event.keyName?.let { inviteUserToEvent(event) }
        }
    }

    private fun setInviteUserRecycleAdapter() {
        userRecyclerView = findViewById<RecyclerView>(R.id.userInviteRecycleView)
        userRecyclerView?.layoutManager = LinearLayoutManager(this)

        val userAdapter = InviteRecycleAdapter(this)
        userRecyclerView?.adapter = userAdapter
        userAdapter.updateItemsToList(FriendDataManager.friendsList)

        userRecyclerView?.let { FriendDataManager.setFirebaseListenerForFriends(it) }
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        event = intent.getSerializableExtra(EVENT_EXTRA) as Event
        EventDataManager.inviteList = event.invitedUsers!!
    }

    private fun inviteUserToEvent(event : Event) {
        event.invitedUsers = EventDataManager.inviteList
        EventDataManager.inviteFriends(event)
        finish()
    }

/*    private fun inviteUserToEvent(eventKeyName : String, event : Event) {
        // Set not attend as a default for new invites.
        event.attend = false
        // Go through the list of invites and get a collection path with their userID.
        val inviteList = FriendDataManager.inviteList
        for (user in inviteList){
            val inviteRef = user.userID?.let {
                EventDataManager.db.collection("users").document(it).collection("userEvents")
            }
            // When the collection path is set add a new document with the event.
            inviteRef?.document(eventKeyName)?.set(event)
        }
        inviteList.clear()
    }*/
}
