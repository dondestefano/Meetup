package com.example.meetup.data_managers

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.util.*

object UserDataManager {
    // All users //
    var loggedInUser = User(null, null, null)
    val allUsersList = mutableListOf<User>()

    // Database-helpers //
    var db = FirebaseFirestore.getInstance()
    val allUsersRef = db.collection("users")
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var userDataRef : DocumentReference //QUESTION: Do I need this?

    fun getLoggedInUser(context: Context) {
        val loggedInUserID = auth.currentUser?.uid
        userDataRef = loggedInUserID?.let { allUsersRef.document(it) }!!
        if(loggedInUserID != null) {
            userDataRef?.addSnapshotListener { snapshot, e ->
                // Load user with the correct id from Firebase
                if (snapshot != null) {
                    loggedInUser = snapshot.toObject(User::class.java)!!
                    Toast.makeText(context, "Welcome ${UserDataManager.loggedInUser.name}!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Error fetching user", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }

    fun setFirebaseListenerForUsers(userRecyclerView: RecyclerView?) {
        allUsersRef.addSnapshotListener { snapshot, e ->
            // Clear list
            allUsersList.clear()
            // Load all users from Firebase
            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val loadUser = document.toObject(User::class.java)
                    loadUser?.let { allUsersList.add(it) }
                    userRecyclerView?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun getUser(userID : String): User?  {
        for (user in allUsersList) {
            if (user.userID == userID) {
                return user
            }
        }
        return null
    }

    fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference("/images/$filename")
        imageRef.putFile(selectedPhotoUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                userDataRef.update("profileImageURL", it.toString())
            }
        }
    }

    // Might be of use
    fun downloadImageFromFirebaseStorage(filename: String) {
        val imageRef = FirebaseStorage.getInstance().getReference("/images/$filename")
        imageRef.downloadUrl.addOnSuccessListener {

        }
    }
}