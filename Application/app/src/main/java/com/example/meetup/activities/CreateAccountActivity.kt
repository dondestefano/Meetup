package com.example.meetup.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.meetup.data_managers.UserDataManager
import com.example.meetup.objects.User
import com.example.meetup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Image
var selectedPhotoUri : Uri? = null

class CreateAccountActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var auth: FirebaseAuth
    private lateinit var createUsernameEditText: EditText
    private lateinit var createEmailText: EditText
    private lateinit var createPasswordText: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var chosenPictureImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        createEmailText = findViewById(R.id.createEmailEditText)
        createPasswordText = findViewById(R.id.createPasswordEditText)
        createUsernameEditText = findViewById(R.id.createNameEditText)
        uploadImageButton = findViewById(R.id.addImageButton)
        chosenPictureImageView = findViewById(R.id.chosenPictureImageView)

        val createButton = findViewById<Button>(R.id.createAccountButton)

        createButton.setOnClickListener{
            addAccount()
        }

        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            chosenPictureImageView.setImageBitmap(bitmap)

            // Hide the upload image button to display the image.
            uploadImageButton.visibility = View.GONE

            chosenPictureImageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }
    }

    private fun addAccount() {
        auth.createUserWithEmailAndPassword(createEmailText.text.toString(), createPasswordText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToDatabase()
                    Toast.makeText(this, "User created.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
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

        UserDataManager.allUsersRef.document(userID.toString()).set(
            newUser
        )
        selectedPhotoUri?.let { UserDataManager.uploadImageToFirebaseStorage(it) }
    }
}
