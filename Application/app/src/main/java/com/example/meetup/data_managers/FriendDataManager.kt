package com.example.meetup.data_managers

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.meetup.objects.AdapterItem
import com.example.meetup.objects.Friend
import com.example.meetup.objects.User
import com.example.meetup.recycle_adapters.FriendRecycleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue.delete
import com.google.firebase.firestore.FirebaseFirestore

object FriendDataManager {
    // List //
    val itemsList = mutableListOf<AdapterItem>()

    // Datebase-helpers
    var db = FirebaseFirestore.getInstance()
    private lateinit var currentUser : FirebaseUser
    private lateinit var friendsRef : CollectionReference


    fun setFirebaseListenerForFriends(friendRecyclerView: RecyclerView) {
        friendsRef.addSnapshotListener { snapshot, e ->
            itemsList.clear()
            // Load confirmed friends from database
            if (snapshot != null) {
                // Create temporary sortable lists for confirmed and not confirmed friends
                val friendList = mutableListOf<AdapterItem>()
                val requestList = mutableListOf<AdapterItem>()

                // Search for friends with friendStatus confirmed.
                for (document in snapshot.documents) {
                    val loadFriend = document.toObject(Friend::class.java)
                    if (loadFriend != null && loadFriend.friendStatus == true) {
                        val item = AdapterItem(
                            null, loadFriend,
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
                    friendList.sortBy { it.friend?.name }
                    itemsList.addAll(friendList)
                }

                //Search for firends with status not pending
                for (document in snapshot.documents) {
                    val loadFriend = document.toObject(Friend::class.java)
                    if (loadFriend != null && loadFriend.friendStatus == false) {
                        val item = AdapterItem(
                            null, loadFriend,
                            FriendRecycleAdapter.TYPE_FRIEND
                        )
                        requestList.add(item)
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
                    requestList.sortBy { it.friend?.name }
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
        currentUser?.let { friendsRef = db.collection("users").document(it.uid).collection("friends") }
    }

    fun addFriend(user: User) {
        val newFriend = Friend(user.name, user.email, user.userID, false)
        newFriend.userID?.let { friendsRef?.document(it)?.set(newFriend) }
    }

    fun removeFriend(context: Context, user: User) {
        user.userID?.let { friendsRef?.document(it)
            ?.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Removed ${user.name} from friends.", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "Error. Cant't remove ${user.name} from friends..", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}