package com.example.meetup.DataManagers

import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.Objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object UserDataManager {

    var loggedInUser = User(null, null, null)
    val allUsersList = mutableListOf<User>()
    val inviteList = mutableListOf<User>()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()
    val allUsersRef = db.collection("users")
    lateinit var userDataRef : DocumentReference //QUESTION: Do I need this?

    fun getLoggedInUser() {
        val loggedInUserID = auth.currentUser?.uid
        userDataRef = loggedInUserID?.let { allUsersRef.document(it) }!!
        if(loggedInUserID != null) {
            userDataRef?.addSnapshotListener { snapshot, e ->
                // Load user with teh correct
                if (snapshot != null) {
                    loggedInUser = snapshot.toObject(User::class.java)!!
                    println("!!! ${loggedInUser.name}")
                } else {
                    println("!!! $e")
                }
            }
        }
    }

    fun setFirebaseListenerForUsers(userRecyclerView: RecyclerView) {
        allUsersRef.addSnapshotListener { snapshot, e ->
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