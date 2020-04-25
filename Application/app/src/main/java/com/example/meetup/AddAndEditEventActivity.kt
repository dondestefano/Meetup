package com.example.meetup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import kotlinx.android.synthetic.main.activity_add_new_event.*
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.FEBRUARY
import javax.xml.datatype.DatatypeConstants.MONTHS

const val EVENT_POSITION_NOT_SET = -1
const val EVENT_POSITION_KEY = "EVENT_POSITION"
const val EVENT_LIST_KEY = "EVENT_LIST"
const val EVENT_LIST_NOT_SET = "NO_LIST"

class AddAndEditEventActivity : AppCompatActivity() {
    private val calendar: Calendar = Calendar.getInstance()
    lateinit var timeEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var nameEditText : EditText
    lateinit var saveButton : Button
    private var eventPosition = EVENT_POSITION_NOT_SET
    private var eventList = EVENT_LIST_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        nameEditText = findViewById(R.id.nameEditText)
        saveButton = findViewById(R.id.saveButton)

        val stringExtra = intent.getStringExtra(EVENT_LIST_KEY)
        eventPosition = intent.getIntExtra(EVENT_POSITION_KEY, eventPosition)
        if (stringExtra != null) {
            eventList = stringExtra
        }



        setOnClickListeners()
        setDateAndTime()
    }


    private fun setOnClickListeners() {
        dateEditText.setOnClickListener {
            pickDate()
        }
        timeEditText.setOnClickListener{
            pickTime()
        }
        saveButton.setOnClickListener{
            if (eventPosition != EVENT_POSITION_NOT_SET) {
                editEvent(eventPosition)
            } else {
                addEvent()
            }
        }
    }

    private fun addEvent() {
        val name = nameEditText.text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm")
        val formatedDate = dateFormat.format(calendar.time)
        val date = dateFormat.parse(formatedDate)
        val event = Event(name, date, true)
        EventDataManager.attendingEvents.add(event)
        finish()
    }

    private fun editEvent(position: Int) {
        val name = nameEditText.text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm")
        val formatedDate = dateFormat.format(calendar.time)
        val date = dateFormat.parse(formatedDate)

        if(eventList == "attending") {
            EventDataManager.attendingEvents[position].name = name
            EventDataManager.attendingEvents[position].date = date
        } else {
            EventDataManager.declinedEvents[position].name = name
            EventDataManager.declinedEvents[position].date = date
        }
        finish()
    }

    private fun pickDate() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, selectedYear, monthValue, dayOfMonth ->

                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.DAY_OF_YEAR, dayOfMonth)
                calendar.time //Don't ask...
                calendar.set(Calendar.MONTH, monthValue)
                val newDate = EventDataManager.dateFormat.format(calendar.time)
                dateEditText.setText(newDate)
            }, year, month, day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun pickTime() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val newTime = EventDataManager.timeFormat.format(calendar.time)
            timeEditText.setText(newTime)
        }
        TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            .show()
    }

    private fun setDateAndTime() {
        var date : String
        var time : String
        var event : Event
        var name = "Event name"

        if (eventList == "attending") {
            event = EventDataManager.attendingEvents[eventPosition]
            date = EventDataManager.dateFormat.format(event.date)
            time = EventDataManager.timeFormat.format(event.date)
            name = event.name
        }

        else if (eventList == "declined") {
            event = EventDataManager.declinedEvents[eventPosition]
            date = EventDataManager.dateFormat.format(event.date)
            time = EventDataManager.timeFormat.format(event.date)
            name = event.name
        }

        else {
            date = EventDataManager.dateFormat.format(calendar.time)
            time = EventDataManager.timeFormat.format(calendar.time)
        }

        dateEditText.setText(date)
        timeEditText.setText(time)
        nameEditText.setText(name)
    }
}

