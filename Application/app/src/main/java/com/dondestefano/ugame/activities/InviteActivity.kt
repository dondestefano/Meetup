package com.dondestefano.ugame.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.R
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.data_managers.FriendDataManager
import com.dondestefano.ugame.objects.Event
import com.dondestefano.ugame.recycle_adapters.InviteRecycleAdapter

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
        if (event.invitedUsers?.isNotEmpty()!!) {
            inviteButton.setOnClickListener {
                event.keyName?.let { inviteAdditional(event) }
            }
        } else {
            inviteButton.setOnClickListener {
                event.keyName?.let { inviteUserToEvent(event) }
            }
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
        EventDataManager.inviteList.clear()
        EventDataManager.inviteList.addAll(event.invitedUsers!!)
    }

    private fun inviteUserToEvent(event : Event) {
        EventDataManager.inviteFriends(event)
        finish()
    }

    private fun inviteAdditional(event: Event) {
        EventDataManager.inviteAdditionalFriends(event)
        finish()
    }
}
