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
import javax.xml.datatype.DatatypeConstants.MONTHS

class AddEventActivity : AppCompatActivity() {
    val calendar = Calendar.getInstance()
    lateinit var timeEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var saveButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        saveButton = findViewById(R.id.saveButton)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val date = EventDataManager.dateFormat.format(calendar.time)
        val time = EventDataManager.timeFormat.format(calendar.time)
        dateEditText.setText(date)
        timeEditText.setText(time)


        dateEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker, selectedYear, selectedMonth, dayOfMonth ->
                    calendar.set(Calendar.YEAR, selectedYear)
                    calendar.set(Calendar.MONTH, selectedMonth)
                    calendar.set(Calendar.DAY_OF_YEAR, dayOfMonth)
                    val newDate = EventDataManager.dateFormat.format(calendar.time)
                    dateEditText.setText(newDate)
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        timeEditText.setOnClickListener{
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val newTime = EventDataManager.timeFormat.format(calendar.time)
                timeEditText.setText(newTime)
            }
            TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        saveButton.setOnClickListener{
            addEvent()
        }

    }

    fun addEvent() {
        val name = nameEditText.text.toString()

        val dateFormat = SimpleDateFormat("yyyy-mm-dd-HH-mm")
        val formatedDate = dateFormat.format(calendar.getTime())
        val date = dateFormat.parse(formatedDate)

        val event = Event(name, date, true)
        EventDataManager.attendingEvents.add(event)
        finish()
    }

}

