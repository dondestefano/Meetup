package com.example.meetup.DataManagers

import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.Objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserDataManager {

    var loggedInUser = User(null, null, null)
    val allUsersList = mutableListOf<User>()
    val inviteList = mutableListOf<User>()
    var db = FirebaseFirestore.getInstance()
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val userRef = db.collection("users")

    
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

    fun updateUserToFirebase() {
        userRef?.document(loggedInUser.userID.toString()).set(loggedInUser)
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