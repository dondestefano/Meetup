package com.example.meetup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.attendButton)
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
        }
    }
}
