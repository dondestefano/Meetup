package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

class ListActivity : AppCompatActivity() {
    private var eventRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setEventRecycleAdapters()
        setFabButton()
        eventRecyclerView?.let { EventDataManager.setFirebaseListener(it) }
    }

    override fun onResume() {
        super.onResume()
        eventRecyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun setEventRecycleAdapters() {
        eventRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        eventRecyclerView?.layoutManager = LinearLayoutManager(this)

        val eventAdapter = EventRecycleAdapter(this)
        eventAdapter.updateItemsToList(EventDataManager.itemsList)
        eventRecyclerView?.adapter = eventAdapter

    }

    private fun setFabButton() {
        val fab = findViewById<View>(R.id.addEventActionButton)
        fab.setOnClickListener{ view ->
            val intent = Intent(this, AddAndEditEventActivity::class.java)
            intent.putExtra("EVENT_POSITION", "NO_LIST")
            startActivity(intent)
        }
    }
}
