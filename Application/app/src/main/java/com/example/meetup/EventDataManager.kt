package com.example.meetup

import java.text.SimpleDateFormat
import java.util.*

object EventDataManager {
    val attendingEvents = mutableListOf<Event>()
    val declinedEvents = mutableListOf<Event>()
    val dateFormat = SimpleDateFormat("E dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")

    fun sortLists() {
        attendingEvents.sortBy {it.date}
        declinedEvents.sortBy {it.date}
    }
}

