package com.example.meetup.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.objects.Event
import com.example.meetup.R
import com.example.meetup.recycle_adapters.InviteRecycleAdapter

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

        setInviteUserRecycleAdapter()
        userRecyclerView?.let { UserDataManager.setFirebaseListenerForUsers(it) }
    }

    fun setOnclickListeners() {
        inviteButton.setOnClickListener {
            event.keyName?.let { inviteUserToEvent(it, event) }
            finish()
        }
    }


    private fun setInviteUserRecycleAdapter() {
        userRecyclerView = findViewById<RecyclerView>(R.id.userInviteRecycleView)
        userRecyclerView?.layoutManager = LinearLayoutManager(this)

        val userAdapter = InviteRecycleAdapter(this)
        userRecyclerView?.adapter = userAdapter
        userAdapter.updateItemsToList(UserDataManager.allUsersList)
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        event = intent.getSerializableExtra(EVENT_EXTRA) as Event
    }

    private fun inviteUserToEvent(eventKeyName : String, event : Event) {
        // Set not attend as a default for new invites.
        event.attend = false
        // Go through the list of invites and get a collection path with their userID.
        val inviteList = UserDataManager.inviteList
        for (user in inviteList){
            val inviteRef = user.userID?.let {
                EventDataManager.db.collection("users").document(it).collection("userEvents")
            }
            // When the collection path is set add a new document with the event.
            if (inviteRef != null) {
                inviteRef.document(eventKeyName).set(event)
            }
        }
        inviteList.clear()
    }
}
