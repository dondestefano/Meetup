package com.example.meetup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

const val EVENT_POSITION_NOT_SET = -1
const val EVENT_POSITION_KEY = "EVENT_POSITION"
const val EVENT_LIST_KEY = "EVENT_LIST"
const val EVENT_LIST_NOT_SET = "NO_LIST"

class AddAndEditEventActivity : AppCompatActivity() {
    // Layout assets.
    lateinit var timeEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var nameEditText : EditText
    lateinit var saveButton : Button

    // New/editable event and calendar.
    private lateinit var event : Event
    private val calendar: Calendar = Calendar.getInstance()

    // Put extra helpers.
    private var eventPosition = EVENT_POSITION_NOT_SET
    private var eventList = EVENT_LIST_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        nameEditText = findViewById(R.id.nameEditText)
        saveButton = findViewById(R.id.saveButton)

        getExtraFromIntent()
        setOnClickListeners()
        setDateAndTime()
    }

    private fun getExtraFromIntent() {
        val stringExtra = intent.getStringExtra(EVENT_LIST_KEY)
        // Get the event's position
        eventPosition = intent.getIntExtra(EVENT_POSITION_KEY, eventPosition)

        //Get the event's list
        if (stringExtra != null) {
            eventList = stringExtra
        }
    }

    private fun setOnClickListeners() {
        dateEditText.setOnClickListener {
            pickDate()
        }
        timeEditText.setOnClickListener{
            pickTime()
        }

        // Determine if the saveButton should create a new event or edit an existing event.
        saveButton.setOnClickListener{
            if (eventPosition != EVENT_POSITION_NOT_SET) {
                editEvent(eventPosition)
            } else {
                saveButton.text = "Add"
                addEvent()
            }
        }
    }

    private fun addEvent() {
        val name = nameEditText.text.toString()
        val date : Date = calendar.time
        event.changeDate(date)
        event.changeName(name)

        EventDataManager.attendingEvents.add(event)
        finish()
    }

    private fun editEvent(position: Int) {
        val name = nameEditText.text.toString()
        val date : Date = calendar.time
        event.changeDate(date)
        event.changeName(name)

        // Get the event from the correct list
        if(eventList == "attending") {
            EventDataManager.attendingEvents[position] = event

        } else {
            EventDataManager.declinedEvents[position] = event
        }
        finish()
    }

    private fun pickDate() {
        // Get the calendars date so that it can be used with the datepicker.
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, selectedYear, monthValue, dayOfMonth ->

                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                calendar.time //Don't ask...
                calendar.set(Calendar.MONTH, monthValue)
                val newDate = EventDataManager.dateFormat.format(calendar.time)

                // Update text and the save button with the new information.
                dateEditText.setText(newDate)
            }, year, month, day
        )

        // Prevent the user from choosing a date that has already passed.
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
        val date : String
        val time : String

        // Determine if the event is new or if the user wants to edit it.
        // If the user wants to edit the date determine which list it's in.
        if (eventList == "attending") {
            event = EventDataManager.attendingEvents[eventPosition]
            calendar.time = event.date
            setDateForEventToEdit()
        }

        else if (eventList == "declined") {
            event = EventDataManager.declinedEvents[eventPosition]
            calendar.time = event.date
            setDateForEventToEdit()
        }

        // Get the current time and date if the user wants to add a new event.
        else {
            val currentDate = calendar.time
            date = EventDataManager.dateFormat.format(currentDate)
            time = EventDataManager.timeFormat.format(currentDate)
            dateEditText.setText(date)
            timeEditText.setText(time)

            // Set base data for a new event
            event = Event("name", currentDate, true)
        }
    }

    fun setDateForEventToEdit() {
        val date = EventDataManager.dateFormat.format(event.date)
        val time = EventDataManager.timeFormat.format(event.date)
        val name = event.name

        dateEditText.setText(date)
        timeEditText.setText(time)
        nameEditText.setText(name)
    }
}

