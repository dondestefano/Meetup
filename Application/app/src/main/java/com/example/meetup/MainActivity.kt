package com.example.meetup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {

    var attending = listOf<Event>(Event("Play CS", Date(2020, 11, 25, 12, 12), false), Event("Eat Bagle", Date(2020, 12, 25, 12, 12), false))
    var notAttending = listOf<Event>(Event("Play Valorant", Date(2020, 11, 22, 12, 12), false), Event("Pizza Time", Date(2020, 12, 21, 12, 12), false))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val attendRecyclerView = findViewById<RecyclerView>(R.id.attendRecyclerView)
        val notAttendRecyclerView = findViewById<RecyclerView>(R.id.notAttendRecyclerView)

        attendRecyclerView.layoutManager = LinearLayoutManager(this)
        notAttendRecyclerView.layoutManager = LinearLayoutManager(this)

        val attendAdapter = EventRecycleAdapter(this, attending)
        val notAttendAdapter = EventRecycleAdapter(this, notAttending)

        attendRecyclerView.adapter = attendAdapter
        notAttendRecyclerView.adapter = notAttendAdapter




        /*val button = findViewById<Button>(R.id.attendButton)
        val dateTextView = findViewById<TextView>(R.id.testTextView)
        val nameTextView = findViewById<TextView>(R.id.eventName)

        var now = Event("Play CS", Date(2020, 11, 25, 12, 12), false)


            val a = now.date.hours.toString()
            val b = now.date.minutes.toString()

            nameTextView.text = now.name.toString()
            dateTextView.text = "$a:$b"
            checkAttend(now)



            button.setOnClickListener {
                now.changeAttend()
                checkAttend(now)
            }
        }

        fun checkAttend(event: Event) {
            if (event.attend) {
                testTextView.setTextColor(Color.GREEN)
            } else {testTextView.setTextColor(Color.RED)
        }*/
    }

    fun arrangeEvents() {
        
    }
}
