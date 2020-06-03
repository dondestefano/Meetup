package com.dondestefano.ugame.data_managers

import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.objects.AdapterItem
import com.dondestefano.ugame.objects.Event
import com.dondestefano.ugame.recycle_adapters.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

const val EVENT_PATH = "events"
const val EVENT_COLLECTION_PATH = "userEvents"

object EventDataManager {
    //* Lists *//
    val itemsList = mutableListOf<AdapterItem>() // List for recycleview
    var inviteList = mutableListOf<String>() // List for inviting friends to events

    //* Dateformatters *//
    val dateFormat = SimpleDateFormat("E dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")

    //* Datebase-helpers *//
    var db = FirebaseFirestore.getInstance()
    private var currentUser : FirebaseUser? = null
    private lateinit var eventRef : CollectionReference


    // Data listeners //

    fun resetEventDataManagerUser() {
        // Get the current users information for the EventDataManager
        currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            eventRef = db.collection(EVENT_PATH).document(it.uid).collection(EVENT_COLLECTION_PATH)
        }
    }

    fun setFirebaseListener(eventRecyclerView: RecyclerView) {
        eventRef.addSnapshotListener { snapshot, e ->
            // Clear list
            itemsList.clear()
            // Load attending events from Firebase
            if (snapshot != null) {
                // Create temporary sortable lists for attending and not attending
                val newInviteList = mutableListOf<AdapterItem>()
                val attendList = mutableListOf<AdapterItem>()
                val declineList = mutableListOf<AdapterItem>()

                // Check for and remove passed events.
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && checkIfEventHasPassed(loadEvent)) {
                        removeEvent(loadEvent)
                    }
                }

                // Search for events with status new.
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.new == true) {
                        val item = AdapterItem(
                            loadEvent, null,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        // Sort the event as new if the user isn't the host.
                        if (loadEvent.host != UserDataManager.loggedInUser.userID) {
                            newInviteList.add(item)
                        }
                        // Sort the event as attending if the user is the host.
                        else { attendList.add(item) }
                    }
                }

                if (newInviteList.isNotEmpty()) {
                    // Add attend header
                    val newHeader = AdapterItem(
                        null, null,
                        EventRecycleAdapter.TYPE_NEW_HEADER
                    )
                    itemsList.add(newHeader)
                    // Sort attend list before adding it to itemsList
                    attendList.sortBy { it.event?.date }
                    itemsList.addAll(newInviteList)
                }

                // Search for events with status attending.
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.attend == true && loadEvent.new == false) {
                        val item = AdapterItem(
                            loadEvent, null,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        attendList.add(item)
                    }
                }

                if (attendList.isNotEmpty()) {
                    // Add attend header
                    val attendHeader = AdapterItem(
                        null, null,
                        EventRecycleAdapter.TYPE_ACCEPT_HEADER
                    )
                    itemsList.add(attendHeader)
                    // Sort attend list before adding it to itemsList
                    attendList.sortBy { it.event?.date }
                    itemsList.addAll(attendList)
                }

                //Search for events with status declined.
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.attend == false && loadEvent.new == false) {
                        val item = AdapterItem(
                            loadEvent, null,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        declineList.add(item)
                    }
                }

                if (declineList.isNotEmpty()) {
                    // Add decline header
                    val declineHeader = AdapterItem(
                        null, null,
                        EventRecycleAdapter.TYPE_DECLINE_HEADER
                    )
                    itemsList.add(declineHeader)
                    // Sort decline list before adding it to itemsList
                    declineList.sortBy { it.event?.date }
                    itemsList.addAll(declineList)
                }
                // Notify changes to the adapter when the async data has been loaded
                eventRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    fun updateEventToFirebase(eventKey : String, event : Event) {
        eventRef.document(eventKey).set(event)
    }

    fun updateEventDetailsToFireBase(event: Event) {
        val date = event.date
        val name = event.name
        val invitedUsers = event.invitedUsers

        for (friendID in event.invitedUsers!!) {
            val friendEventRef = db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH)
            event.keyName?.let { friendEventRef.document(it).update(
                "date", date,
                "name", name,
                "invitedUsers", invitedUsers) }
        }

        event.keyName?.let { eventRef.document(it).update(
            "date", date,
            "name", name,
            "invitedUsers", invitedUsers)
        }
    }

    fun checkAttendance(event: Event, guestListRecyclerView: RecyclerView, guestListRecycleAdapter: GuestListRecycleAdapter, guestStatus: String) {
        // Create a list to save each guest with attend = true.
        val acceptedInvites = mutableListOf<com.dondestefano.ugame.objects.User>()
        val declinedInvites = mutableListOf<com.dondestefano.ugame.objects.User>()
        val newInvites = mutableListOf<com.dondestefano.ugame.objects.User>()
        // Add the host of the event by default.
        val host = event?.host?.let { UserDataManager.getUser(it) }
        acceptedInvites.add(host!!)

        for (friendID in event.invitedUsers!!) {
            // Check friends event document to get their status.
            println("!!! $guestStatus")
            event.keyName?.let {
                db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH).document(
                    it
                )
            }?.addSnapshotListener(){ snapshot, e ->
                // Check accepted invites in Firebase
                if (snapshot != null) {
                    // Get the status for attend and new to determine which list to put the guest in.
                    val status = snapshot.data?.getValue("attend")
                    val checkNew = snapshot.data?.getValue("new")
                    when  {
                        status == true && checkNew == false -> {
                            val guest = UserDataManager.getUser(friendID)
                            acceptedInvites.add(guest!!)
                            declinedInvites.remove(guest)
                            newInvites.remove(guest)
                            println("!!! Found accepted")
                        }

                        status == false && checkNew == false -> {
                            val declinedGuest = UserDataManager.getUser(friendID)
                            acceptedInvites.remove(declinedGuest)
                            declinedInvites.add(declinedGuest!!)
                            newInvites.remove(declinedGuest)
                            println("!!! Found declined")
                        }

                        checkNew == true ->  {
                            val newGuest = UserDataManager.getUser(friendID)
                            newInvites.add(newGuest!!)
                            println("!!! Found new")
                        }
                    }
                    // Assign the correct list to the adapter.
                    when (guestStatus) {
                        GUEST_LIST_ATTEND -> {
                            guestListRecycleAdapter.updateGuestList(acceptedInvites)
                        }

                        GUEST_LIST_DECLINED -> {
                            guestListRecycleAdapter.updateGuestList(declinedInvites)
                        }

                        GUEST_LIST_NEW -> {
                            guestListRecycleAdapter.updateGuestList(newInvites)
                        }
                    }
                    // Tell the view to update.
                    guestListRecyclerView.adapter?.notifyDataSetChanged()
                    guestListRecyclerView.scheduleLayoutAnimation()
                }
            }
        }
    }

    fun removeEvent(event: Event) {
        // If the host removes the event
        // Remove the event from invited friends as well.
        if (UserDataManager.loggedInUser.userID == event.host) {
            for (friendID in event.invitedUsers!!) {
                val friendEventRef = db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH)
                event.keyName?.let { friendEventRef.document(it).delete() }
            }
        }

        // Remove the event from current users events.
        event.keyName?.let { eventRef.document(it).delete() }
    }

    fun inviteFriends(event: Event) {
        // Save the invited users information in the event.
        event.invitedUsers = inviteList
        event.attend = false
        for (friendID in inviteList) {
            val friendEventRef = db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH)
            event.keyName?.let { friendEventRef.document(it).set(event) }
        }
        event.keyName?.let { updateEventToFirebase(it, event) }
    }

    fun inviteAdditionalFriends(event: Event) {
        val newInvites = mutableListOf<String>()
        // Add all currently selected users.
        newInvites.addAll(inviteList)
        // Remove all users who already had been invited.
        for (invited in event.invitedUsers!!) {
            newInvites.remove(invited)
        }
        // Set the new status of the event.
        event.attend = false
        event.invitedUsers = inviteList
        for (friendID in newInvites) {
            val friendEventRef = db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH)
            event.keyName?.let { friendEventRef.document(it).set(event) }
        }
        // Update the event for all users.
        updateEventDetailsToFireBase(event)
    }

    private fun checkIfEventHasPassed(event: Event): Boolean {
        // Get the current date.
        val now = Calendar.getInstance(Locale.getDefault())

        // Get the events date + one day.
        val eventTimePassed = Calendar.getInstance(Locale.getDefault())
        event?.date?.let {
            eventTimePassed.set(Calendar.HOUR_OF_DAY, 0)
            eventTimePassed.set(Calendar.MINUTE, 0)
            eventTimePassed.set(Calendar.SECOND, 0)
            eventTimePassed.set(Calendar.MONTH, it.month)
            eventTimePassed.set(Calendar.DAY_OF_WEEK, it.day + 2) // Date days start at 0. Add two to get the day after.
        }
        return now.time >= eventTimePassed.time
    }
}