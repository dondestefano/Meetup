package com.example.meetup.Activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.DataManagers.EventDataManager
import com.example.meetup.RecycleAdapters.EventRecycleAdapter
import com.example.meetup.R
import com.google.firebase.auth.FirebaseAuth

class ListActivity : AppCompatActivity() {
    private var eventRecyclerView : RecyclerView? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        setEventRecycleAdapters()
        setFabButtons()
        eventRecyclerView?.let { EventDataManager.setFirebaseListener(it) }
    }

    override fun onResume() {
        super.onResume()
        eventRecyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun setEventRecycleAdapters() {
        eventRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        eventRecyclerView?.layoutManager = LinearLayoutManager(this)

        val eventAdapter =
            EventRecycleAdapter(this)
        eventAdapter.updateItemsToList(EventDataManager.itemsList)
        eventRecyclerView?.adapter = eventAdapter

    }

    private fun setFabButtons() {
        val fab = findViewById<View>(R.id.addEventActionButton)
        fab.setOnClickListener{ view ->
            val intent = Intent(this, AddAndEditEventActivity::class.java)
            intent.putExtra("EVENT_POSITION", "NO_LIST")
            startActivity(intent)
        }

        val logoutFab = findViewById<View>(R.id.logoutButton)
        logoutFab.setOnClickListener{
            auth.signOut()
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}