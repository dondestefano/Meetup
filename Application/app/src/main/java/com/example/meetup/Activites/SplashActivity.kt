package com.example.meetup.Activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.meetup.DataManagers.UserDataManager
import com.example.meetup.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val notLoggedInIntent = Intent(this, LoginActivity::class.java)
        val loggedInIntent = Intent(this, ListActivity::class.java)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(notLoggedInIntent)
        } else {
            UserDataManager.getLoggedInUser()
            UserDataManager.updateUserToFirebase()
            startActivity(loggedInIntent)

            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT)
                .show()
            (println("!!! ${FirebaseAuth.getInstance().currentUser?.email}"))

            finish()
        }
    }
}