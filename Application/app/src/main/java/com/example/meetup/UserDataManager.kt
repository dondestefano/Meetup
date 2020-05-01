package com.example.meetup

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserDataManager {

    var loggedInUser = User(null, null, null)
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    
    fun getUser() {
        val user = auth.currentUser
        if (user != null) {
            loggedInUser.name = user.displayName.toString()
            loggedInUser.email = user.email.toString()
            loggedInUser.userID = user.uid.toString()
        } else {
            return
        }
    }
}