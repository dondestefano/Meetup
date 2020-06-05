package com.dondestefano.ugame.data_managers

import android.content.Context
import android.service.autofill.UserData
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dondestefano.ugame.activities.*
import com.dondestefano.ugame.objects.AdapterItem
import com.dondestefano.ugame.objects.User
import com.dondestefano.ugame.recycle_adapters.FriendRecycleAdapter
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
    // Lists //
    val itemsList = mutableListOf<AdapterItem>()
    val friendsList = mutableListOf<User>()

    // Database-helpers //
    private var db = FirebaseFirestore.getInstance()
    private lateinit var currentUser : FirebaseUser
    private lateinit var userReqRef : CollectionReference
    private lateinit var friendReqRef : CollectionReference



    fun setFirebaseListenerForFriends(friendRecyclerView: RecyclerView) {
        userReqRef.addSnapshotListener { snapshot, e ->
            itemsList.clear()
            friendsList.clear()
            if (snapshot != null) {
                // Create temporary sortable lists for requested, confirmed and not confirmed friends
                val newRequestList = mutableListOf<AdapterItem>()
                val friendList = mutableListOf<AdapterItem>()
                val requestList = mutableListOf<AdapterItem>()

                ///    Load new friend requests from database   ///

                // Search for friends with friendStatus confirmed in Firebase.
                for (document in snapshot.documents) {
                    val status = document.data?.getValue("status")
                    if (status == REQUEST_RECEIVED) {
                        // Find the friend with the correct ID from UserDataManager
                        // and add it to to friendList as a  ListItem.
                        val friend = UserDataManager.getUser(document.id)
                        val item = AdapterItem(
                            null, friend,
                            FriendRecycleAdapter.TYPE_FRIEND
                        )
                        newRequestList.add(item)
                    }
                }

                if(newRequestList.isNotEmpty()) {
                    // Add friends header
                    val newRequestHeader = AdapterItem(
                        null, null,
                        FriendRecycleAdapter.TYPE_REQUESTED_HEADER
                    )
                    itemsList.add(newRequestHeader)
                    // Sort friend list before adding it to itemsList
                    newRequestList.sortBy { it.user?.name }
                    itemsList.addAll(newRequestList)
                }

                ///    Load accepted friends from database   ///

                // Search for friends with friendStatus confirmed in Firebase.
                for (document in snapshot.documents) {
                    val status = document.data?.getValue("status")
                    if (status == REQUEST_ACCEPTED) {
                        // Find the friend with the correct ID from UserDataManager
                        // and add it to to friendList as a  ListItem.
                        val friend = UserDataManager.getUser(document.id)

                        // Save confirmed friends in a separate list for invite uses.
                        friend?.let { friendsList.add(it) }

                        val item = AdapterItem(
                            null, friend,
                            FriendRecycleAdapter.TYPE_FRIEND
                        )
                        friendList.add(item)
                    }
                }

                if(friendList.isNotEmpty()) {
                    // Add friends header
                    val friendHeader = AdapterItem(
                        null, null,
                        FriendRecycleAdapter.TYPE_FRIEND_HEADER
                    )
                    itemsList.add(friendHeader)
                    // Sort friend list before adding it to itemsList
                    friendList.sortBy { it.user?.name }
                    itemsList.addAll(friendList)
                }

                ///    Load pending friend requests from database   ///

                //Search for friends who have not yet responded to a request in Firebase.
                for (document in snapshot.documents) {
                    val status = document.data?.getValue("status")
                    if (status == REQUEST_SENT) {
                        // Find the friend with the correct ID from UserDataManager
                        // and add it to requestList as a  ListItem.
                        val friend = UserDataManager.getUser(document.id)
                        val item = AdapterItem(
                            null, friend,
                            FriendRecycleAdapter.TYPE_FRIEND
                        )
                        requestList.add(item)
                    }
                }

                if(requestList.isNotEmpty()) {
                    // Add pending header
                    val pendingHeader = AdapterItem(
                        null, null,
                        FriendRecycleAdapter.TYPE_WAITING_HEADER
                    )
                    itemsList.add(pendingHeader)
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
        currentUser.uid.let { userReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
    }

    fun sendFriendRequest(friend: User) {
        // Send request to friend and upload to friends friendRequests collection in Firebase
        friend.userID?.let { friendReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
        val requestReceived = hashMapOf(
            "status" to REQUEST_RECEIVED,
            "to" to friend.userID,
            "from" to UserDataManager.loggedInUser.userID
        )
        UserDataManager.loggedInUser.userID?.let { friendReqRef.document(it).set(requestReceived as Map<String, String>) }

        // Add request to user and upload to the users friendRequests collection in Firebase
        val request = hashMapOf(
            "status" to REQUEST_SENT,
            "to" to friend.userID,
            "from" to UserDataManager.loggedInUser.userID
        )
        friend.userID?.let { userReqRef.document(it).set(request as Map<String, String>) }
    }

    fun acceptFriendRequest(friend: User) {
        // Send acceptance to friend and update friendRequests document in Firebase
        friend.userID?.let { friendReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
        UserDataManager.loggedInUser.userID?.let { friendReqRef.document(it).update("status", REQUEST_ACCEPTED) }

        // Update request as accepted in firebase
        friend.userID?.let { userReqRef.document(it).update("status", REQUEST_ACCEPTED)}
    }

    fun removeFriend(context: Context, friend: User) {
        // Remove request from friend and upload to friends friendRequests collection in Firebase
        friend.userID?.let { friendReqRef = db.collection(FRIEND_REQUEST_PATH).document(it).collection(REQUEST_PATH) }
        UserDataManager.loggedInUser.userID?.let { friendReqRef.document(it)}
            ?.delete()

        // Remove request from user and upload to the users friendRequests collection in Firebase
        friend.userID?.let { userReqRef.document(it) }
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
                    activity.stateDetermined(status)
                } else {
                    status = STRANGER_STATE
                    activity.stateDetermined(status)
                }
            }
            .addOnFailureListener {
            println("!!! error")
            }
    }
}