package com.example.meetup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var createEmailText: EditText
    lateinit var createPasswordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        createEmailText = findViewById(R.id.createEmailEditText)
        createPasswordText = findViewById(R.id.createPasswordEditText)

        val createButton = findViewById<Button>(R.id.createAccountButton)

        createButton.setOnClickListener{
            addAccount()
        }

    }

    fun addAccount() {
        auth.createUserWithEmailAndPassword(createEmailText.text.toString(), createPasswordText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
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
}
