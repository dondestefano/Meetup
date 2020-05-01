package com.example.meetup

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


object EventDataManager {
    val events = mutableListOf<Event>()
    val dateFormat = SimpleDateFormat("E dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")
    var db = FirebaseFirestore.getInstance()
    private val eventRef = db.collection("events")


   fun setFirebaseListener(eventRecyclerView: RecyclerView) {
        eventRef.addSnapshotListener { snapshot, e ->
            events.clear()
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val loadEvent = document.toObject(Event::class.java)
                    if (loadEvent != null) {
                        events.add(loadEvent!!)
                        events.sortBy { it.date }
                        eventRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    fun updateEventToFirebase(ref : String, event : Event) {
        eventRef.document(ref).set(event)
    }
}

