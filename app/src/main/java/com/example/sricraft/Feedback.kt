package com.example.sricraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Feedback : AppCompatActivity() {

    // Get the current user ID
    private val auth = FirebaseAuth.getInstance()
    var currentUserID = auth.currentUser?.uid;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val nameEditText = findViewById<EditText>(R.id.feedback_name)
        val messageEditText = findViewById<EditText>(R.id.feedback_message)
        val emailEditText = findViewById<EditText>(R.id.feedback_email)
        val btnSubmit = findViewById<Button>(R.id.feedback_submit)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)


        //Send data to database
        btnSubmit.setOnClickListener {

            //Get user input
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val message = messageEditText.text.toString()
            val rating = ratingBar.rating

            //Validate data
            var validated = true

            // Validate name
            if (name.isEmpty()) {
                nameEditText.error = "Name is required"
                nameEditText.requestFocus()
                validated = false
            }

            // Validate email
            else if (email.isEmpty()) {
                emailEditText.error = "Email is required"
                emailEditText.requestFocus()
                validated = false

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Please enter a valid email address"
                emailEditText.requestFocus()
                validated = false
            }

            else if (message.isEmpty()) {
                messageEditText.error = "Message is required"
                messageEditText.requestFocus()
                validated = false
            }

            //Send data to database
            if(validated) {
                // Get a reference to the Firebase Realtime Database
                val database = FirebaseDatabase.getInstance().reference

                // Get a reference to the "feedback" node in the database
                val feedbackRef = database.child("feedback")

                // Get a reference to a new location and add some data using push()
                val key = feedbackRef.push().key

                // Create a HashMap to hold the data to be sent
                val data = hashMapOf(
                    "id" to key.toString(),
                    "name" to name,
                    "email" to email,
                    "message" to message,
                    "rating" to rating.toString(),
                    "userId" to currentUserID
                )

                // setValue method to set the data at the new key
                feedbackRef.child(key.toString()).setValue(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Feedback sent successfully", Toast.LENGTH_SHORT).show()
                        //Go to market activity
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        //Go to market activity
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
            }
        }
    }
}