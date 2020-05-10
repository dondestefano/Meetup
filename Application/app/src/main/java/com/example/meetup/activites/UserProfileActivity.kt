package com.example.meetup.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R

const val USER_POSITION = "USER_POSITION"


class UserProfileActivity : AppCompatActivity() {
    private lateinit var userNameText: TextView
    private lateinit var userImageView: ImageView
    private lateinit var addFriendButton: Button

    // User being displayed
    private var user: com.example.meetup.objects.User? = null


    // Put extra helpers.
    private var userPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userNameText = findViewById(R.id.profileNameTextView)
        userImageView = findViewById(R.id.profilePictureImageView)

        getExtraFromIntent()
        setupUser()
        setOnClickListener()
    }

    private fun getExtraFromIntent() {
        // Get the event's position
        userPosition = intent.getIntExtra(USER_POSITION, userPosition)
        user = UserDataManager.allUsersList[userPosition]
    }

    private fun setupUser() {
        userNameText.text = user?.name
    }

    private fun setOnClickListener() {
        addFriendButton = findViewById(R.id.sendFriendRequestButton)
        addFriendButton.setOnClickListener{
            user?.let { FriendDataManager.addFriend(it) }
            finish()
        }
    }
}
