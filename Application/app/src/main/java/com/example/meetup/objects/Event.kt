package com.example.meetup.objects

import com.example.meetup.data_managers.EventDataManager
import java.io.Serializable
import java.util.*

data class Event(var name: String? = null,
                 var date: Date? = null,
                 var attend: Boolean? = null,
                 var keyName: String? = null) : Serializable  {

    fun changeAttend() {
        attend = !attend!!
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }

    fun changeName(name : String) {
        this.name = name
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }

    fun changeDate(date : Date) {
        this.date = date
        EventDataManager.updateEventToFirebase(this.keyName!!, this)
    }
}