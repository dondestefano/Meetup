package com.example.meetup.DataManagers

import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.Objects.AdapterItem
import com.example.meetup.Objects.Event
import com.example.meetup.Objects.User
import com.example.meetup.RecycleAdapters.EventRecycleAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat


object EventDataManager {
    val itemsList = mutableListOf<AdapterItem>()

    val dateFormat = SimpleDateFormat("E dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")

    var db = FirebaseFirestore.getInstance()
    private val eventRef = UserDataManager.loggedInUser.userID?.let { db.collection("userEvents").document(it).collection("events") }

    fun setFirebaseListener(eventRecyclerView: RecyclerView) {

        eventRef?.addSnapshotListener { snapshot, e ->
            // Clear list
            itemsList.clear()

            // Load attending events from Firebase
            if (snapshot != null) {
                // Create temporary sortable lists for attending and not attending
                val attendList = mutableListOf<AdapterItem>()
                val declineList = mutableListOf<AdapterItem>()

                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.attend == true) {
                        val item = AdapterItem(
                            loadEvent,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        attendList.add(item)
                    }
                }

                if(attendList.isNotEmpty()) {
                    // Add attend header
                    val attendHeader = AdapterItem(
                        null,
                        EventRecycleAdapter.TYPE_ACCEPT_HEADER
                    )
                    itemsList.add(attendHeader)
                    // Sort attend list before adding it to itemsList
                    attendList.sortBy { it.event?.date }
                    itemsList.addAll(attendList)
                }

                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.attend == false) {
                        val item = AdapterItem(
                            loadEvent,
                            EventRecycleAdapter.TYPE_EVENT
                        )
                        declineList.add(item)
                    }
                }
                if(declineList.isNotEmpty()) {
                    // Add decline header
                    val declineHeader = AdapterItem(
                        null,
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

    fun updateEventToFirebase(eventKeyName : String, event : Event) {
        eventRef?.document(eventKeyName)?.set(event)
    }

    fun inviteUserToEvent(eventKeyName : String, event : Event) {
        val inviteList = UserDataManager.inviteList
        for (user in inviteList){
            val inviteRef = user.userID?.let {
                db.collection("userEvents").document(it).collection("events")
            }

            if (inviteRef != null) {
                inviteRef.document(eventKeyName).set(event)
            }
        }
        inviteList.clear()
    }
}

