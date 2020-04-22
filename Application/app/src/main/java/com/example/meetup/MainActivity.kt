package com.example.meetup

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val attendRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        val notAttendRecyclerView = findViewById<RecyclerView>(R.id.notAttendRecyclerView)

        attendRecyclerView.layoutManager = LinearLayoutManager(this)
        notAttendRecyclerView.layoutManager = LinearLayoutManager(this)

        val attendAdapter = EventRecycleAdapter(this, EventDataManager.attendingEvents)
        val notAttendAdapter = EventRecycleAdapter(this, EventDataManager.declinedEvents)

        attendRecyclerView.adapter = attendAdapter
        notAttendRecyclerView.adapter = notAttendAdapter

        val fab = findViewById<View>(R.id.addEventActionButton)
        fab.setOnClickListener{ view ->
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        attendRecyclerView.adapter?.notifyDataSetChanged()
        notAttendRecyclerView.adapter?.notifyDataSetChanged()
    }
}
