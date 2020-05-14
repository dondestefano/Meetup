package com.example.meetup.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.meetup.data_managers.EventDataManager
import com.example.meetup.data_managers.FriendDataManager
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.R
import com.example.meetup.objects.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var textEmail: EditText
    lateinit var passwordText: EditText
    lateinit var createAccount : TextView
    lateinit var loginButton : Button
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        textEmail = findViewById(R.id.emailEditText)
        passwordText = findViewById(R.id.passwordEditText)
        createAccount = findViewById(R.id.createAccTextView)
        loginButton = findViewById<Button>(R.id.loginButton)


        loginButton.setOnClickListener {
            login()
        }

        createAccount.setOnClickListener{
            goToCreateAccountActivity()
        }
    }

    private fun login() {
        auth.signInWithEmailAndPassword(textEmail.text.toString(), passwordText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    UserDataManager.getLoggedInUser()
                    EventDataManager.resetEventDataManagerUser()
                    FriendDataManager.resetFriendDataManagerUser()
                    UserDataManager.setFirebaseListenerForUsers(null)
                    goToListActivity()
                    Toast.makeText(this, "Welcome ${UserDataManager.loggedInUser.name}!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this, "Wrong e-mail or password.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun goToListActivity() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun goToCreateAccountActivity() {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }
}