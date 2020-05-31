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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.objects.Event
import com.example.meetup.R
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.recycle_adapters.GUEST_LIST_ATTEND
import com.example.meetup.recycle_adapters.GUEST_LIST_DECLINED
import com.example.meetup.recycle_adapters.GUEST_LIST_NEW
import com.example.meetup.recycle_adapters.GuestListRecycleAdapter
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
    // Event assets resources.
    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var nameEditText : EditText
    // Guest status resources.
    private lateinit var attendRecyclerView: RecyclerView
    private lateinit var declineRecyclerView: RecyclerView
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var guestStatusLayout: ConstraintLayout
    // Event functionality resources.
    private lateinit var toolbar: Toolbar
    private lateinit var inviteFabButton : ExtendedFloatingActionButton
    private lateinit var speedDialFab : SpeedDialView

    // New/editable event and calendar.
    private var event : Event? = null
    private val calendar: Calendar = Calendar.getInstance()

    // Set position as not set by default.
    private var eventPosition = EVENT_POSITION_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_event)

        // Get views.
        timeEditText = findViewById(R.id.timeEditText)
        dateEditText = findViewById(R.id.dateEditText)
        nameEditText = findViewById(R.id.nameEditText)
        inviteFabButton = findViewById(R.id.inviteFabButton)
        speedDialFab = findViewById(R.id.speedDialFabView)
        guestStatusLayout = findViewById(R.id.guestStatusLayout)

        // Setup activity
        getExtraFromIntent()
        setupToolbar()
        determineAddOrEdit()

        // Set speedDialFunctionality
        speedDialFab.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                return false
            }
            override fun onToggleChanged(isOpen: Boolean) {
            }
        })
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        eventPosition = intent.getIntExtra(EVENT_POSITION_KEY, eventPosition)
    }

    //* Setup functionality *//

    private fun setupToolbar() {
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarEvent)
        setSupportActionBar(toolbar)

        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.close)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Closes the activity when pressing the close button.
        finish()
        return false
    }

    private fun setupRadialFabButtonHost() {
        // Setup each individual action button for host functionality.
        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.saveChangesFab, R.drawable.edit)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme
                ))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorWhite, theme))
                .setLabel("Save changes")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.inviteFriendsFab, R.drawable.add_friend)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, getTheme()))
                .setLabel("Invite friends")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .setLabelClickable(false)
                .create())

        speedDialFab.addActionItem(
            SpeedDialActionItem.Builder(R.id.cancelEventFab, R.drawable.remove)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_error, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, getTheme()))
                .setLabel("Cancel event")
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_error, getTheme()))
                .setLabelClickable(false)
                .create())

        // Set functions for speedDial buttons.
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
                        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                            event?.let { EventDataManager.removeEvent(it) }
                            speedDialFab.close()
                            finish()
                        })
                        .setNegativeButton("No", DialogInterface.OnClickListener {
                                dialog, id -> dialog.cancel()
                        })
                        .show()
                }
            }
            false
        })
    }

    private fun setUpSpeedDialFabInvited() {
        // Setup each individual action button for attendance functionality.
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
                    attendRecyclerView.adapter?.notifyDataSetChanged()

                    return@OnActionSelectedListener true
                }

                R.id.declineFab -> {
                    event?.changeAttend(false)
                    speedDialFab.close()
                    attendRecyclerView.adapter?.notifyDataSetChanged()
                    return@OnActionSelectedListener true
                }
            }
            false
        })
    }

    private fun setupRecycleViews() {
        println("!!! Doing it")
        attendRecyclerView = findViewById(R.id.attendingListRecyclerView)
        declineRecyclerView = findViewById(R.id.declinedListRecyclerView)
        newRecyclerView = findViewById(R.id.invitedListRecyclerView)

        // Setup accepted adapter.
        attendRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val attendingGuestsAdapter = GuestListRecycleAdapter(this)
        attendRecyclerView.adapter = attendingGuestsAdapter
        EventDataManager.checkAttendance(event!!, attendRecyclerView, attendingGuestsAdapter, GUEST_LIST_ATTEND)

        // Setup declined adapter.
        declineRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val declinedGuestsAdapter = GuestListRecycleAdapter(this)
        declineRecyclerView.adapter = declinedGuestsAdapter
        println("!!! This is the ${event?.name} ")
        EventDataManager.checkAttendance(event!!, declineRecyclerView, declinedGuestsAdapter, GUEST_LIST_DECLINED)

        // Setup new adapter.
        newRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val newGuestsAdapter = GuestListRecycleAdapter(this)
        newRecyclerView.adapter = newGuestsAdapter
        EventDataManager.checkAttendance(event!!, newRecyclerView, newGuestsAdapter, GUEST_LIST_NEW)
    }

    private fun setOnClickListeners() {
        inviteFabButton.setOnClickListener {
            goToInvite()
        }

        dateEditText.setOnClickListener {
            pickDate()
        }

        timeEditText.setOnClickListener{
            pickTime()
        }
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
            finish()
        }
        else {
            errorToast("Please enter a name for your event.")
        }
    }

    //* Edit event *//

    private fun determineAddOrEdit() {
        val date : String
        val time : String

        // If the user wants to edit the event get it from the EventDataManagers itemsList.
        if (eventPosition != EVENT_POSITION_NOT_SET) {
            event = EventDataManager.itemsList[eventPosition].event
            event?.let {event ->
                calendar.time = event.date
                setDateForEventToEdit()
            }

            setupRecycleViews()

            // Set the events invite list as the data manager's invited list.
            EventDataManager.inviteList = event?.invitedUsers!!

            // If the user is the host setup the radial menu for editing
            if (event?.host == UserDataManager.loggedInUser.userID) {
                setupRadialFabButtonHost()
                setOnClickListeners()
            }
            // If the user is not the host setup attendance radial menu
            // and disable editing.
            else {
                setUpSpeedDialFabInvited()
                nameEditText.isFocusable = false
                dateEditText.isFocusable = false
                timeEditText.isFocusable = false
            }
        }

        // If the user wants to add a new event get the current time and date.
        else {
            val currentDate = calendar.time
            date = EventDataManager.dateFormat.format(currentDate)
            time = EventDataManager.timeFormat.format(currentDate)
            dateEditText.setText(date)
            timeEditText.setText(time)

            inviteFabButton.visibility = VISIBLE
            speedDialFab.visibility = GONE

            // Set base data for a new event
            val inviteList = mutableListOf<String>()
            event = Event(
                "name",
                currentDate,
                true,
                true,
                null,
                UserDataManager.loggedInUser.userID,
                inviteList)

            // Hide guest status for new events.
            guestStatusLayout.visibility = GONE

            // Set on click listeners to enable edit functionality.
            setOnClickListeners()
        }
    }

    private fun setDateForEventToEdit() {
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

