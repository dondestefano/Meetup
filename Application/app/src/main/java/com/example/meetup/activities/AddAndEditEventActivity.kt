package com.example.meetup.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.objects.Event
import com.example.meetup.R
import com.example.meetup.data_managers.UserDataManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.activity_add_new_event.*
import kotlinx.android.synthetic.main.activity_add_new_event.view.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.cert.Extension
import java.util.*

const val EVENT_POSITION_NOT_SET = -1
const val EVENT_POSITION_KEY = "EVENT_POSITION"

class AddAndEditEventActivity : AppCompatActivity() {
    // Layout assets.
    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var nameEditText : EditText
    private lateinit var inviteFabButton : ExtendedFloatingActionButton
    private lateinit var speedDialFab : SpeedDialView

    // New/editable event and calendar.
    private var event : Event? = null
    private val calendar: Calendar = Calendar.getInstance()

    // Put extra helpers.
    private var eventPosition =
        EVENT_POSITION_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        nameEditText = findViewById(R.id.nameEditText)
        inviteFabButton = findViewById(R.id.inviteFabButton)
        speedDialFab = findViewById(R.id.speedDialFabView)


        getExtraFromIntent()
        setOnClickListeners()
        determineAddOrEdit()

        speedDialFab.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
            }
        })
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        eventPosition = intent.getIntExtra(EVENT_POSITION_KEY, eventPosition)
    }

    private fun setOnClickListeners() {
        inviteFabButton.setOnClickListener {
            goToInvite()
            finish()
        }

        dateEditText.setOnClickListener {
            pickDate()
        }

        timeEditText.setOnClickListener{
            pickTime()
        }
    }

    private fun setupRadialFabButtonHost() {
        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.saveChangesFab, R.drawable.edit)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, getTheme()))
                .setLabel("Save changes")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.inviteFriendsFab, R.drawable.add_friend)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, getTheme()))
                .setLabel("Invite friends")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.cancelEventFab, R.drawable.remove)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_error, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, getTheme()))
                .setLabel("Cancel event")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_error, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.saveChangesFab -> {
                    editEvent(eventPosition)
                    speedDialFab.close()
                    return@OnActionSelectedListener true
                    finish()
                }

                R.id.inviteFriendsFab -> {
                    goToInvite()
                    speedDialFab.close()
                    return@OnActionSelectedListener true
                    finish()
                }

                R.id.cancelEventFab -> {
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setTitle("Cancel this event?")
                        .setMessage("Are you sure you want to cancel and remove this event?")
                        .setPositiveButton("Remove", DialogInterface.OnClickListener { dialog, id ->
                            event?.let { EventDataManager.removeEvent(it) }
                            speedDialFab.close()
                            finish()
                        })
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                dialog, id -> dialog.cancel()
                        })
                        .show()
                }
            }
            false
        })
    }

    private fun setUpSpeedDialFabInvited() {
        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.attendFab, R.drawable.approve)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPositive, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, getTheme()))
                .setLabel("I'm game!")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPositive, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.declineFab, R.drawable.decline)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_error, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, getTheme()))
                .setLabel("No game for me.")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_error, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.attendFab -> {
                    event?.changeAttend(true)
                    speedDialFab.close()
                    finish()
                    return@OnActionSelectedListener true
                }

                R.id.declineFab -> {
                    event?.changeAttend(false)
                    speedDialFab.close()
                    finish()
                    return@OnActionSelectedListener true
                }
            }
            false
        })
    }


    //* Invite friends *//

    private fun goToInvite() {
        val name = nameEditText.text.toString()
        // Check if the user has entered a name for the event.
        if (name.isNotEmpty()) {
            // If the event is new clear invite list and save the event
            // before continuing.
            if (eventPosition == EVENT_POSITION_NOT_SET) {
                EventDataManager.inviteList.clear()
                addEvent()
            } else { EventDataManager.inviteList = event?.invitedUsers!! }

            val intent = Intent(this, InviteActivity::class.java)
            intent.putExtra("EVENT", event)
            startActivity(intent)
        }
        else {
            errorToast("Please enter a name for your event.")
        }
    }

    //* Edit event *//

    private fun determineAddOrEdit() {
        val date : String
        val time : String

        // Determine if the event is new or if the user wants to edit it.
        // If the user wants to edit the event determine which list it's in.
        if (eventPosition != EVENT_POSITION_NOT_SET) {
            event = EventDataManager.itemsList[eventPosition].event
            event?.let {event ->
                calendar.time = event.date
                setDateForEventToEdit()
            }

            // Set the events invite list as the data manager's invited list
            EventDataManager.inviteList = event?.invitedUsers!!

            // If the user is the host setup the radial menu for editing
            if (event?.host == UserDataManager.loggedInUser.userID) {
                setupRadialFabButtonHost()
            } else { setUpSpeedDialFabInvited() }
        }

        // Get the current time and date if the user wants to add a new event.
        else {
            val currentDate = calendar.time
            date = EventDataManager.dateFormat.format(currentDate)
            time = EventDataManager.timeFormat.format(currentDate)
            dateEditText.setText(date)
            timeEditText.setText(time)

            inviteFabButton.visibility = VISIBLE
            speedDialFab.visibility = GONE

            // Set base data for a new event
            val list = mutableListOf<String>()
            event = Event(
                "name",
                currentDate,
                true,
                true,
                null,
                UserDataManager.loggedInUser.userID,
                list)
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


    private fun addEvent() {
        val name = nameEditText.text.toString()

        if (name.isNotEmpty()) {
            val date : Date = calendar.time
            event?.let { event ->
                val eventKey = UUID.randomUUID().toString()
                event.date = date
                event.keyName = eventKey
                event.name = name
                event.keyName?.let { EventDataManager.updateEventToFirebase(it, event) }
            }
        } else {
            errorToast("Please enter a name for your event.")
        }
    }

    private fun editEvent(position: Int) {
        val name = nameEditText.text.toString()
        val date : Date = calendar.time
        event?.let { event ->
            event.changeDate(date)
            event.changeName(name)

            EventDataManager.updateEventDetailsToFireBase(event)
            finish()
        }
    }

    private fun errorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .show()
    }
}

