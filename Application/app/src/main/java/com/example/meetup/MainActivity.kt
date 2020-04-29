package com.example.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setEventRecycleAdapters()
        setFabButton()
        EventDataManager.sortLists()
    }

    override fun onResume() {
        super.onResume()
        attendRecyclerView.adapter?.notifyDataSetChanged()
        notAttendRecyclerView.adapter?.notifyDataSetChanged()
        EventDataManager.sortLists()
    }

    fun setEventRecycleAdapters() {
        val attendRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        val notAttendRecyclerView = findViewById<RecyclerView>(R.id.notAttendRecyclerView)

        attendRecyclerView.layoutManager = LinearLayoutManager(this)
        notAttendRecyclerView.layoutManager = LinearLayoutManager(this)


        val attendAdapter = EventRecycleAdapter(this, EventDataManager.attendingEvents, null)
        val notAttendAdapter = EventRecycleAdapter(this, EventDataManager.declinedEvents, null)
        attendAdapter.setOtherAdapter(notAttendAdapter)
        notAttendAdapter.setOtherAdapter(attendAdapter)

        attendRecyclerView.adapter = attendAdapter
        notAttendRecyclerView.adapter = notAttendAdapter
    }

    fun setFabButton() {
        val fab = findViewById<View>(R.id.addEventActionButton)
        fab.setOnClickListener{ view ->
            val intent = Intent(this, AddAndEditEventActivity::class.java)
            intent.putExtra("EVENT_POSITION", "NO_LIST")
            startActivity(intent)

        }
    }
}
