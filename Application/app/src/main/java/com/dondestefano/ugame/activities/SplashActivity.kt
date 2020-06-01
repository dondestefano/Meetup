package com.dondestefano.ugame.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dondestefano.ugame.R
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.data_managers.FriendDataManager
import com.dondestefano.ugame.data_managers.UserDataManager
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
            finish()
        } else {
            // Go directly to ListActivity if the user is already logged in.
            UserDataManager.getLoggedInUser(this)
            UserDataManager.setFirebaseListenerForUsers(null)
            EventDataManager.resetEventDataManagerUser()
            FriendDataManager.resetFriendDataManagerUser()
            startActivity(loggedInIntent)
            finish()
        }
    }
}