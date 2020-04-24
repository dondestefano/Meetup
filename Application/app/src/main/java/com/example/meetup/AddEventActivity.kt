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

class AddEventActivity : AppCompatActivity() {
    private val calendar: Calendar = Calendar.getInstance()
    lateinit var timeEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var saveButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        saveButton = findViewById(R.id.saveButton)

        dateEditText.setOnClickListener {
            pickDate()
        }

        timeEditText.setOnClickListener{
            pickTime()
        }

        saveButton.setOnClickListener{
            addEvent()
        }

        setDateAndTime()
    }

    private fun addEvent() {
        Log.d("hej", calendar.time.toString())

        val name = nameEditText.text.toString()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm")
        val formatedDate = dateFormat.format(calendar.time)
        Log.d("hej2", formatedDate)
        val date = dateFormat.parse(formatedDate)

        val event = Event(name, date, true)
        EventDataManager.attendingEvents.add(event)
        Log.d("hej", event.date.toString())
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
                var newDate = EventDataManager.dateFormat.format(calendar.time)
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

    fun setDateAndTime() {
        val date = EventDataManager.dateFormat.format(calendar.time)
        val time = EventDataManager.timeFormat.format(calendar.time)
        dateEditText.setText(date)
        timeEditText.setText(time)
    }
}

