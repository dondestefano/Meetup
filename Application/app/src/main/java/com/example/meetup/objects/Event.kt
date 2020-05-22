package com.example.meetup.objects

import com.example.meetup.data_managers.EventDataManager
import java.io.Serializable
import java.util.*

data class Event(var name: String? = null,
                 var date: Date? = null,
                 var attend: Boolean? = null,
                 var new: Boolean? = null,
                 var keyName: String? = null,
                 var host: String? = null,
                 var invitedUsers: MutableList<String>? = null ): Serializable {

    fun changeAttend() {
        // Permanently set the event as no longer new.
        new = false

        // Change attend
        attend = !attend!!
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }

    ///////// CHECK IF I NEED THESE
    fun changeName(name : String) {
        this.name = name
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }

    fun changeDate(date : Date) {
        this.date = date
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }
}