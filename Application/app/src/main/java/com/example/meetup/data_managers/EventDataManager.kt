package com.example.meetup.data_managers

import android.service.autofill.UserData
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.objects.AdapterItem
import com.example.meetup.objects.Event
import com.example.meetup.objects.User
import com.example.meetup.recycle_adapters.EventRecycleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

const val INVITE_PATH = "eventInvites"
const val EVENT_COLLECTION = "myEvents"
const val INVITED = "invited"
const val ACCEPTED = "accepted"
const val DECLINED = "declined"
const val CANCELED = "canceled"

object EventDataManager {
    // List //
    val itemsList = mutableListOf<AdapterItem>()
    val inviteList = mutableListOf<String>()

    // Dateformatters //
    val dateFormat = SimpleDateFormat("E dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")

    // Datebase-helpers
    var db = FirebaseFirestore.getInstance()
    private var currentUser : FirebaseUser? = null
    private lateinit var eventRef : CollectionReference
    private lateinit var inviteRef: CollectionReference

    // Data listeners //

    fun resetEventDataManagerUser() {
        // Get the current users information for the EventDataManager
        currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { eventRef = db.collection("users").document(it.uid).collection("userEvents") }
    }

    fun setFirebaseListener(eventRecyclerView: RecyclerView) {
        eventRef.addSnapshotListener { snapshot, e ->
            // Clear list
            itemsList.clear()
            // Load attending events from Firebase
            if (snapshot != null) {
                // Create temporary sortable lists for attending and not attending
                val attendList = mutableListOf<AdapterItem>()
                val declineList = mutableListOf<AdapterItem>()

                // Search for events with status attending.
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.attend == true) {
                        val item = AdapterItem(
                            loadEvent, null,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        attendList.add(item)
                    }
                }

                if(attendList.isNotEmpty()) {
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
                    if (loadEvent != null && loadEvent.attend == false) {
                        val item = AdapterItem(
                            loadEvent, null,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        declineList.add(item)
                    }
                }

                if(declineList.isNotEmpty()) {
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


    // Add and remove events //

    fun updateEventToFirebase(eventKey : String, event : Event) {
        event.invitedUsers = inviteList
        eventRef.document(eventKey).set(event)
        inviteRef = db.collection(INVITE_PATH).document(UserDataManager.loggedInUser.userID!!).collection(EVENT_COLLECTION)
        val eventInvite = hashMapOf(
            "status" to ACCEPTED,
            "host" to UserDataManager.loggedInUser.userID
        )
        event.keyName?.let { inviteRef.document(it).set(eventInvite) }

    }

    fun removeEvent(event: Event) {
        // Delete the event from users events
        eventRef.document(event.keyName!!).delete()
        // Set status to CANCELED for the host.
        var cancelledEvent = UserDataManager.loggedInUser.userID?.let {
            db
                .collection(INVITE_PATH)
                .document(it)
                .collection(EVENT_COLLECTION)
                .document(event.keyName!!)
        }

        cancelledEvent?.update("status", CANCELED)

        // Set status to CANCELED for all invited users.
        for (userID in event.invitedUsers!!) {
            cancelledEvent = db
                .collection(INVITE_PATH)
                .document(userID)
                .collection(EVENT_COLLECTION)
                .document(event.keyName!!)

            cancelledEvent.update("status", CANCELED)
        }
    }

    fun inviteFriends(event: Event) {
        for (userID in inviteList) {
            inviteRef = db.collection(INVITE_PATH).document(userID).collection(EVENT_COLLECTION)
            val eventInvite = hashMapOf(
                "status" to INVITED,
                "host" to UserDataManager.loggedInUser.userID
            )
            event.keyName?.let { inviteRef.document(it).set(eventInvite) }
        }
        event.keyName?.let { updateEventToFirebase(it, event) }
    }
}