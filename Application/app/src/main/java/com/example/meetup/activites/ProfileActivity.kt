package com.example.meetup.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R
import com.example.meetup.objects.User

const val USER_POSITION = "USER_POSITION"
const val STRANGER_STATE = "STRANGER_STATE"
const val USER_STATE = "USER_STATE"
const val SENT_STATE = "SENT_STATE"
const val RECEIVED_STATE = "RECEIVED_STATE"
const val FRIEND_STATE = "FRIEND STATE"


class UserProfileActivity : AppCompatActivity() {
    private lateinit var userNameText: TextView
    private lateinit var userImageView: ImageView
    private lateinit var addFriendButton: Button
    private var currentState = STRANGER_STATE

    // User being displayed
    private var user: User? = null


    // Put extra helpers.
    private var userPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userNameText = findViewById(R.id.profileNameTextView)
        userImageView = findViewById(R.id.profilePictureImageView)

        getExtraFromIntent()
        determineState()
        setUpFromState()
        setupUser()
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        userPosition = intent.getIntExtra(USER_POSITION, userPosition)
        user = UserDataManager.allUsersList[userPosition]
    }

    private fun determineState() {
        if (user?.userID == UserDataManager.loggedInUser.userID) {
            currentState = USER_STATE
        }
        else {
            STRANGER_STATE
        }
    }

    private fun setUpFromState() {
        when (currentState) {
            FRIEND_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = "Remove friend"
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.removeFriend(this, it) }
                    currentState = STRANGER_STATE
                    setUpFromState()
                }
            }
            STRANGER_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = "Add friend"
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.addFriend(it) }
                    currentState = SENT_STATE
                    setUpFromState()
                }
            }
            SENT_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = "Cancel friend request"
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.removeFriend(this, it) }
                    currentState = STRANGER_STATE
                    setUpFromState()
                }
            }
            RECEIVED_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.text = "Accept friend request"
                addFriendButton.setOnClickListener {
                    user?.let { FriendDataManager.addFriend(it) }
                    currentState = FRIEND_STATE
                    setUpFromState()
                }
            }
            USER_STATE -> {
                addFriendButton = findViewById(R.id.sendFriendRequestButton)
                addFriendButton.visibility = GONE;
            }
        }
    }

    private fun setupUser() {
        userNameText.text = user?.name
    }

    private fun setOnClickListenerAddFriend() {
        addFriendButton = findViewById(R.id.sendFriendRequestButton)
        addFriendButton.setOnClickListener{
            user?.let { FriendDataManager.addFriend(it) }
        }
    }
}
