package com.dondestefano.ugame.notification

import android.app.PendingIntent
import android.content.Intent
import android.service.autofill.UserData
import android.util.Log
import android.widget.Toast
import com.dondestefano.ugame.activities.MainActivity
import com.dondestefano.ugame.activities.SplashActivity
import com.dondestefano.ugame.activities.UserProfileActivity
import com.dondestefano.ugame.data_managers.EventDataManager
import com.dondestefano.ugame.data_managers.UserDataManager
import com.dondestefano.ugame.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.NullPointerException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Add the token only if a user is logged in.
        if (FirebaseAuth.getInstance().currentUser != null) {
            addTokenToFirestore(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val remoteMap = remoteMessage.data
        val sender = remoteMap["sender"]
        val friendId = remoteMap["friend"]
        val hostId = remoteMap["hostId"]
        val eventName = remoteMap["eventName"]
        val intentMain = Intent(this, MainActivity::class.java)

        when (sender) {
            "event" -> {
                val host = hostId?.let { UserDataManager.getUser(it) }
                if (host != null) {
                    NotificationHelper.createNotification("Event", this, eventName!!, "${host.name} has invited you. Are you game?", intentMain)
                } else {NotificationHelper.createNotification("Event", this, "New event.", "You have a new invitation. Are you game?", intentMain)}
            }

            "friend" -> {
                val intentFriend = Intent(this, UserProfileActivity::class.java)
                val friend = friendId?.let { UserDataManager.getUser(it) }
                if (friend != null) {
                    // Add the friends id to intent.
                    intentFriend.putExtra("USER_ID", friendId)
                    NotificationHelper.createNotification("Event", this, "New friend request", "${friend.name} wants to be your friend.", intentFriend)
                } else {NotificationHelper.createNotification("Event", this, "New friend request", "Someone wants to be your friend", intentFriend)}
            }
        }
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String) {
            if (newRegistrationToken == null) throw NullPointerException("FCM Token is null")

            UserDataManager.getFCMRegistrationToken { tokens ->
                if (tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationToken
                tokens.add(newRegistrationToken)
                UserDataManager.setFCMRegistrationTokens(tokens)
            }
        }
    }
}