package com.example.sricraft

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ContactUs : AppCompatActivity() {

    // Get the current user ID
    private val auth = FirebaseAuth.getInstance()
    var currentUserID = auth.currentUser?.uid;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        //Floating action button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

        val nameEditText = findViewById<EditText>(R.id.contactus_name)
        val messageEditText = findViewById<EditText>(R.id.contactus_message)
        val emailEditText = findViewById<EditText>(R.id.contactus_email)
        val btnSubmit = findViewById<Button>(R.id.contactus_submit)


        //Send data to database
        btnSubmit.setOnClickListener {

            //Get user input
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val message = messageEditText.text.toString()

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

                // Get a reference to the "contactus" node in the database
                val contactUsRef = database.child("contactus")

                // Get a reference to a new location and add some data using push()
                val key = contactUsRef.push().key

                // Create a HashMap to hold the data to be sent
                val data = hashMapOf(
                    "id" to key.toString(),
                    "name" to name,
                    "email" to email,
                    "message" to message,
                    "userId" to currentUserID
                )

                // setValue method to set the data at the new key
                contactUsRef.child(key.toString()).setValue(data)
                    .addOnSuccessListener {
                       Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
            }
        }
    }
}