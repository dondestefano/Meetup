package com.example.meetup

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


object EventDataManager {
    val attendingEvents = mutableListOf<Event>()
    val declinedEvents = mutableListOf<Event>()
    val dateFormat = SimpleDateFormat("E dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")
    var db = FirebaseFirestore.getInstance()
    private val eventRef = db.collection("events")

    fun sortLists() {
        attendingEvents.sortBy {it.date}
        declinedEvents.sortBy {it.date}
    }

   fun setFirebaseListener() {
        eventRef.addSnapshotListener { snapshot, e ->
            attendingEvents.clear()
            declinedEvents.clear()
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null && loadEvent.attend!!) {
                        attendingEvents.add(loadEvent!!)
                        sortLists()
                    } else if (loadEvent != null) {
                        declinedEvents.add(loadEvent!!)
                        sortLists()
                    }
                }
            }
        }
    }

    fun readFromFirebase() {
        attendingEvents.clear()
        declinedEvents.clear()
        db.collection("events")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val loadEvent = document.toObject(Event::class.java)
                        if (loadEvent != null && loadEvent.attend!!) {
                            attendingEvents.add(loadEvent!!)
                            sortLists()
                        } else if (loadEvent != null) {
                            declinedEvents.add(loadEvent!!)
                            sortLists()
                        }
                    }
                } else {
                    sortLists()
                    Log.d("Hej", attendingEvents.size.toString())
                }
            }
    }

    fun updateEventToFirebase(ref : String, event : Event) {
        eventRef.document(ref).set(event)
    }
}

