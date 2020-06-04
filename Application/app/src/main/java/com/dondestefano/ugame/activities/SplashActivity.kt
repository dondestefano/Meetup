package com.dondestefano.ugame.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dondestefano.ugame.R
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.data_managers.FriendDataManager
import com.dondestefano.ugame.data_managers.UserDataManager
import com.dondestefano.ugame.notification.MyFirebaseMessagingService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val notLoggedInIntent = Intent(this, LoginActivity::class.java)
        val loggedInIntent = Intent(this, MainActivity::class.java)

        // Go to login screen if there's no logged in user.
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(notLoggedInIntent)
            finish()
        } else {
            // Go directly to ListActivity if the user is already logged in.
            // Reset all DataManagers on logging in.
            UserDataManager.getLoggedInUser(this)
            UserDataManager.setFirebaseListenerForUsers(null)
            EventDataManager.resetEventDataManagerUser()
            FriendDataManager.resetFriendDataManagerUser()
            val registrationToken = FirebaseInstanceId.getInstance().token
            if (registrationToken != null) {
                MyFirebaseMessagingService.addTokenToFirestore(registrationToken)
            }
            startActivity(loggedInIntent)
            finish()
        }
    }
}