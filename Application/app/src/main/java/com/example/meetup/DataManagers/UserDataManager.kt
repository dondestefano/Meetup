package com.example.meetup.DataManagers

import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.Objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserDataManager {

    var loggedInUser = User(null, null)
    val allUsersList = mutableListOf<User>()
    val inviteList = mutableListOf<User>()
    var db = FirebaseFirestore.getInstance()
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val userRef = db.collection("users")


    
    fun getLoggedInUser() {
        val loggedInUserID = auth.currentUser?.uid
        if(loggedInUserID != null) {
            userRef.document(loggedInUserID).addSnapshotListener { snapshot, e ->
                // Load user with teh correct
                if (snapshot != null) {
                    loggedInUser = snapshot.toObject(User::class.java)!!
                }
            }
        }
    }


    fun setFirebaseListenerForUsers(userRecyclerView: RecyclerView) {
        userRef.addSnapshotListener { snapshot, e ->
            // Clear list
            allUsersList.clear()
            // Load all users from Firebase
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val loadUser = document.toObject(User::class.java)
                    loadUser?.let { allUsersList.add(it) }
                    userRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}