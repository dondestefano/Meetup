package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        attendRecyclerView.layoutManager = LinearLayoutManager(this)
        notAttendRecyclerView.layoutManager = LinearLayoutManager(this)

        setEventRecycleAdapters()
        setFabButton()
        EventDataManager.setFirebaseListener(attendRecyclerView, notAttendRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        attendRecyclerView.adapter?.notifyDataSetChanged()
        notAttendRecyclerView.adapter?.notifyDataSetChanged()
        EventDataManager.sortLists()
    }

    private fun setEventRecycleAdapters() {
        val attendRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        val notAttendRecyclerView = findViewById<RecyclerView>(R.id.notAttendRecyclerView)

        val attendAdapter = EventRecycleAdapter(this, EventDataManager.attendingEvents, null)
        val notAttendAdapter = EventRecycleAdapter(this, EventDataManager.declinedEvents, null)
        attendAdapter.setOtherAdapter(notAttendAdapter)
        notAttendAdapter.setOtherAdapter(attendAdapter)

        attendRecyclerView.adapter = attendAdapter
        notAttendRecyclerView.adapter = notAttendAdapter
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
