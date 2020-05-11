package com.example.meetup.objects

data class Friend (var name : String? = null,
                   var email : String? = null,
                   var userID: String? = null,
                   var friendStatus : Boolean? = null) {

    fun changeStatus() {
        friendStatus =! friendStatus!!
    }
}