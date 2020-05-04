package com.example.meetup

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserDataManager {

    var loggedInUser = User(null, null, null)
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val allUsersList = mutableListOf<User>()
    var db = FirebaseFirestore.getInstance()
    private val userRef = db.collection("testUsers")

    
    fun getLoggedInUser() {
        val user = auth.currentUser
        if (user != null) {
            loggedInUser.name = user.displayName.toString()
            loggedInUser.email = user.email.toString()
            loggedInUser.userID = user.uid.toString()
        } else {
            return
        }
    }

    fun setFirebaseListenerForUsers(userRecyclerView: RecyclerView) {
        userRef.addSnapshotListener { snapshot, e ->
            // Clear list
            allUsersList.clear()
            println("!!! cleared")
            // Load attending events from Firebase
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    println("!!! $document")
                    val loadUser = document.toObject(User::class.java)
                    println("!! hej")
                    loadUser?.let { allUsersList.add(it) }
                    userRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}