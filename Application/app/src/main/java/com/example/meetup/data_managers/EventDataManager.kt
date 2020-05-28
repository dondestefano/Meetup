package com.example.meetup.data_managers

import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.data_managers.EventDataManager.db
import com.example.meetup.objects.AdapterItem
import com.example.meetup.objects.Event
import com.example.meetup.recycle_adapters.EventRecycleAdapter
import com.example.meetup.recycle_adapters.GuestListRecycleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import java.text.SimpleDateFormat

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

                // Search for events with status attending.
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.new == true) {
                        val item = AdapterItem(
                            loadEvent, null,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        newInviteList.add(item)
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

                //Search for events with status not attending
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

    fun checkAttendance(event: Event, holder: EventRecycleAdapter.EventViewHolder) {
        val acceptedInvites = mutableListOf<com.example.meetup.objects.User>()
        val host = event?.host?.let { UserDataManager.getUser(it) }
        acceptedInvites.add(host!!)

        for (friendID in event.invitedUsers!!) {
            event.keyName?.let {
                db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH).document(
                    it
                )
            }?.addSnapshotListener(){ snapshot, e ->
                // Check accepted invites in Firebase
                if (snapshot != null) {
                    val status = snapshot.data?.getValue("attend")
                    val checkNew = snapshot.data?.getValue("new")
                    if (status == true) {
                        val guest = UserDataManager.getUser(friendID)
                        acceptedInvites.add(guest!!)
                        holder.guestListRecyclerView.adapter?.notifyDataSetChanged()
                    }
                    else {
                        val declinedUser = UserDataManager.getUser(friendID)
                        acceptedInvites.remove(declinedUser) }
                        holder.guestListRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
            holder.setGuestRecycleAdapter(acceptedInvites)
        }
    }


    fun removeEvent(event: Event) {
        // Remove the event from invited friends events
        for (friendID in event.invitedUsers!!) {
            val friendEventRef = db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH)
            event.keyName?.let { friendEventRef.document(it).delete() }
        }

        // Remove the event from hosts events
        event.keyName?.let { eventRef.document(it).delete() }
    }

    fun inviteFriends(event: Event) {
        event.invitedUsers = inviteList
        event.attend = false
        for (friendID in inviteList) {
            val friendEventRef = db.collection(EVENT_PATH).document(friendID).collection(EVENT_COLLECTION_PATH)
            event.keyName?.let { friendEventRef.document(it).set(event) }
        }
        event.keyName?.let { updateEventToFirebase(it, event) }
    }
}