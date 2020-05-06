package com.example.meetup.Objects

import android.content.Context
import android.content.Intent
import com.example.meetup.Activites.InviteActivity
import com.example.meetup.DataManagers.EventDataManager
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