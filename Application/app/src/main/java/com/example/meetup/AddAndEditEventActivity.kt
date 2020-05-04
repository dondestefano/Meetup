package com.example.meetup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

const val EVENT_POSITION_NOT_SET = -1
const val EVENT_POSITION_KEY = "EVENT_POSITION"

class AddAndEditEventActivity : AppCompatActivity() {
    // Layout assets.
    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var nameEditText : EditText
    private lateinit var inviteButton : Button
    private lateinit var saveButton : Button

    // New/editable event and calendar.
    private var event : Event? = null
    private val calendar: Calendar = Calendar.getInstance()

    // Put extra helpers.
    private var eventPosition = EVENT_POSITION_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        nameEditText = findViewById(R.id.nameEditText)
        inviteButton = findViewById(R.id.inviteButton)
        saveButton = findViewById(R.id.saveButton)

        getExtraFromIntent()
        setOnClickListeners()
        determineAddOrEdit()
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        eventPosition = intent.getIntExtra(EVENT_POSITION_KEY, eventPosition)
    }

    private fun setOnClickListeners() {
        dateEditText.setOnClickListener {
            pickDate()
        }
        timeEditText.setOnClickListener{
            pickTime()
        }

        inviteButton.setOnClickListener{
            val intent = Intent(this, InviteActivity::class.java)
            startActivity(intent)
        }

        // Determine if the saveButton should create a new event or edit an existing event.
        saveButton.setOnClickListener{
            if (eventPosition != EVENT_POSITION_NOT_SET) {
                editEvent(eventPosition)
                saveButton.text = "Save"
            } else {
                saveButton.text = "Add"
                addEvent()
            }
        }
    }

    private fun addEvent() {
        val name = nameEditText.text.toString()

        if (name.isNotEmpty()) {
            val date : Date = calendar.time
            event?.let { event ->
                event.date = date
                event.keyName = name
                event.name = name
                event.name?.let { EventDataManager.updateEventToFirebase(it, event) }
                finish()
            }
        } else {
            Toast.makeText(this, "Cannot make event without a name", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun editEvent(position: Int) {
        val name = nameEditText.text.toString()
        val date : Date = calendar.time
        event?.let { event ->
            event.changeDate(date)
            event.changeName(name)

            event.keyName?.let { EventDataManager.updateEventToFirebase(it, event) }
            finish()
        }
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

    private fun determineAddOrEdit() {
        val date : String
        val time : String

        // Determine if the event is new or if the user wants to edit it.
        // If the user wants to edit the date determine which list it's in.
        if (eventPosition != EVENT_POSITION_NOT_SET) {
            event = EventDataManager.itemsList[eventPosition].event
            event?.let {event ->
                calendar.time = event.date
                setDateForEventToEdit()
            }

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
            saveButton.text = "Add"
        }
    }

    fun setDateForEventToEdit() {
        event?.let { event ->
            val date = EventDataManager.dateFormat.format(event.date)
            val time = EventDataManager.timeFormat.format(event.date)
            val name = event.name

            dateEditText.setText(date)
            timeEditText.setText(time)
            nameEditText.setText(name)
            saveButton.text = "Save"
        }
    }
}

