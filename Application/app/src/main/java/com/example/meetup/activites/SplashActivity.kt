package com.example.meetup.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val notLoggedInIntent = Intent(this, LoginActivity::class.java)
        val loggedInIntent = Intent(this, ListActivity::class.java)

        // Go to login screen if there's no logged in user.
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(notLoggedInIntent)
        } else {
            // Go directly to ListActivity if the user is already logged in.
            UserDataManager.getLoggedInUser()
            EventDataManager.resetEventDataManagerUser()
            FriendDataManager.resetFriendDataManagerUser()
            startActivity(loggedInIntent)

            Toast.makeText(this, "Welcome! ${UserDataManager.loggedInUser.name}!", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
}