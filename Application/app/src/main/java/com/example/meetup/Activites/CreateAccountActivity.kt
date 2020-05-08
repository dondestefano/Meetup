package com.example.meetup.Activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.meetup.DataManagers.UserDataManager
import com.example.meetup.Objects.User
import com.example.meetup.R
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var createUsernameEditText: EditText
    lateinit var createEmailText: EditText
    lateinit var createPasswordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        createEmailText = findViewById(R.id.createEmailEditText)
        createPasswordText = findViewById(R.id.createPasswordEditText)
        createUsernameEditText = findViewById(R.id.createNameEditText)

        val createButton = findViewById<Button>(R.id.createAccountButton)

        createButton.setOnClickListener{
            addAccount()
        }
    }

    fun addAccount() {
        auth.createUserWithEmailAndPassword(createEmailText.text.toString(), createPasswordText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToDatabase()
                    Toast.makeText(this, "User created.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    println("!!! user NOT created!")
                    Toast.makeText(this, "User not created.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun saveUserToDatabase() {
        val currentUser = auth.currentUser
        val userID = currentUser?.uid
        val email = currentUser?.email.toString()
        val name = createUsernameEditText.text.toString()
        val newUser = User(name, email, userID)

        UserDataManager.userRef?.document(userID.toString()).set(
            newUser
        )

    }
}
