package com.example.meetup

import java.util.*

class Event(var name: String, var date: Date, var attend: Boolean)  {

    fun changeAttend() {
        attend = !attend
    }

    fun changeName(name : String) {
        this.name = name
    }

    fun changeDate(date : Date) {
        this.date = date
    }
}