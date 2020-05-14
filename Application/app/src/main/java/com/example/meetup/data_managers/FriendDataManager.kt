package com.example.meetup.data_managers

import android.content.Context
import android.service.autofill.UserData
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.activites.*
import com.example.meetup.objects.AdapterItem
import com.example.meetup.objects.User
import com.example.meetup.recycle_adapters.FriendRecycleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

const val REQUEST_SENT = "sent"
const val REQUEST_RECEIVED = "received"
const val REQUEST_ACCEPTED = "accepted"
const val FRIEND_REQUEST_PATH = "friendRequests"
const val REQUEST_PATH = "requests"

object FriendDataManager {
    // List //
    val itemsList = mutableListOf<AdapterItem>()

    // Database-helpers //
    private var db = FirebaseFirestore.getInstance()
    private lateinit var currentUser : FirebaseUser
    private lateinit var userReqRef : CollectionReference
    private lateinit var friendReqRef : CollectionReference



    fun setFirebaseListenerForFriends(friendRecyclerView: RecyclerView) {
        userReqRef.addSnapshotListener { snapshot, e ->
            itemsList.clear()
            // Load confirmed friends from database
            if (snapshot != null) {
                // Create temporary sortable lists for confirmed and not confirmed friends
                val friendList = mutableListOf<AdapterItem>()
                val requestList = mutableListOf<AdapterItem>()

                // Search for friends with friendStatus confirmed.
                for (document in snapshot.documents) {
                    val status = document.data?.getValue("status")
                    if (status == REQUEST_ACCEPTED) {
                        val friend = UserDataManager.getUser(document.id)
                        val item = AdapterItem(
                            null, friend,
                            FriendRecycleAdapter.TYPE_FRIEND
                        )
                        friendList.add(item)
                    }
                }

                if(friendList.isNotEmpty()) {
                    // Add friends header
                    val requestHeader = AdapterItem(
                        null, null,
                        FriendRecycleAdapter.TYPE_FRIEND_HEADER
                    )
                    itemsList.add(requestHeader)
                    // Sort friend list before adding it to itemsList
                    friendList.sortBy { it.user?.name }
                    itemsList.addAll(friendList)
                }

                //Search for friends who have not yet responded to a request
                for (document in snapshot.documents) {
                    val status = document.data?.getValue("status")
                    if (status == REQUEST_SENT || status == REQUEST_RECEIVED) {
                        val friend = UserDataManager.getUser(document.id)
                        val item = AdapterItem(
                            null, friend,
                            FriendRecycleAdapter.TYPE_FRIEND
                        )
                        requestList.add(item)
                        println("!!! name here yo ${item.user?.name}")
                    }
                }

                if(requestList.isNotEmpty()) {
                    // Add pending header
                    val declineHeader = AdapterItem(
                        null, null,
                        FriendRecycleAdapter.TYPE_WAITING_HEADER
                    )
                    itemsList.add(declineHeader)
                    // Sort pending list before adding it to itemsList
                    requestList.sortBy { it.user?.name }
                    itemsList.addAll(requestList)
                }
                // Notify changes to the adapter when the async data has been loaded
                friendRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }



    fun resetFriendDataManagerUser() {
        // Get the current users information for the EventDataManager
        currentUser = FirebaseAuth.getInstance().currentUser!!
        currentUser?.uid.let { userReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
    }

    fun sendFriendRequest(friend: User) {
        // Send request to friend
        friend.userID?.let { friendReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
        val requestReceived = hashMapOf(
            "status" to REQUEST_RECEIVED
        )
        UserDataManager.loggedInUser.userID?.let { friendReqRef?.document(it)?.set(requestReceived as Map<String, String>) }

        // Add request to user
        val request = hashMapOf(
            "status" to REQUEST_SENT
        )
        friend.userID?.let { userReqRef?.document(it)?.set(request as Map<String, String>) }
    }

    fun acceptFriendRequest(friend: User) {
        // Send acceptance to friend
        friend.userID?.let { friendReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
        val requestAccepted = hashMapOf(
            "status" to REQUEST_ACCEPTED
        )
        UserDataManager.loggedInUser.userID?.let { friendReqRef.document(it).set(requestAccepted as Map<String, String>) }

        // Set request as accepted
        val request = hashMapOf(
            "status" to REQUEST_ACCEPTED
        )
        friend.userID?.let { userReqRef?.document(it)?.set(request as Map<String, String>) }
    }

    fun removeFriend(context: Context, friend: User) {
        // Remove request from friend
        friend.userID?.let { friendReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
        UserDataManager.loggedInUser.userID?.let { friendReqRef?.document(it)}
            ?.delete()

        // Remove request from user
        friend.userID?.let { userReqRef?.document(it) }
            ?.delete()
            ?.addOnSuccessListener {
                Toast.makeText(context, "Removed ${friend.name} from friends.", Toast.LENGTH_SHORT)
                        .show()
            }
            ?.addOnFailureListener{
                Toast.makeText(context, "Error. Cant't remove ${friend.name} from friend list.", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun checkStatus(friendID : String, activity: UserProfileActivity) {
        var status: String
        userReqRef.document(friendID).get()
            .addOnSuccessListener { document ->
                // If the document exist get its state and send it back.
                status = document.data?.getValue("status")?.toString().toString()
                if (status != "null") {
                    println("!!! state is: $status")
                    activity.stateDetermined(status)
                } else {
                    status = STRANGER_STATE
                    println("!!! state is: $status")
                    activity.stateDetermined(status)
                }
            }
            .addOnFailureListener {
            println("!!! error")
            }
    }
}