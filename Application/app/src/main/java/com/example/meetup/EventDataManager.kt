package com.example.meetup

import java.text.SimpleDateFormat
import java.util.*

object EventDataManager {
    val attendingEvents = mutableListOf<Event>()
    val declinedEvents = mutableListOf<Event>()
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("hh:mm")

    init {

    }

    /*fun mockupEvents() {
        var event =  Event("Play CS", Date(2020, 11, 25, 12, 12), true)
        attendingEvents.add(event)
        event = Event("Eat Bagle", Date(2020, 12, 25, 12, 12), true)
        attendingEvents.add(event)

        event = Event("Play Valorant", Date(2020, 11, 22, 12, 12), false)
        declinedEvents.add(event)
        event = Event("Pizza Time", Date(2020, 12, 21, 12, 12), false)
        declinedEvents.add(event)
    }*/
}

