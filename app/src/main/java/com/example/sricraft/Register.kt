package com.example.sricraft

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

//Import database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//Import user model
import com.models.sricraft.User

class Register : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    // Create a function to register the user
    private fun registerUser(name: String, phoneNumber: String, userType: String, email:String, password: String) {
        // Call the Firebase Auth createUserWithEmailAndPassword() method to create a new user account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Create a UserProfileChangeRequest to set the user's display name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    //User ID
                    val uid = auth.currentUser?.uid

                    // Call the Firebase Auth updateProfile() method to update the user's profile with their name
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // The user's display name has been updated
                                Log.d(TAG, "User profile updated.")

                                //Save user data to database
                                try {
                                    // Initialize Firebase database
                                    val database = Firebase.database.reference

                                    // Create a new user object
                                    var newUser = User(uid.toString(), name, email, phoneNumber, userType)

                                    // Set the user data in the database using setValue() method
                                    database.child("users").child(uid.toString()).setValue(newUser)
                                    Toast.makeText(this@Register, "Registration Success", Toast.LENGTH_SHORT)
                                        .show()

                                    //Go to login page
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)

                                } catch (e: Exception) {
                                    // Handle any exceptions that occur
                                    Log.e("FirebaseDatabase", "Error adding user: ${e.message}")
                                }

                            } else {
                                // There was an error updating the user's profile
                                Log.e(TAG, "Error updating user profile.", updateTask.exception)
                            }
                        }
                } else {
                    // There was an error creating the user account
                    Log.e(TAG, "Error creating user account.", task.exception)
                    Toast.makeText(this@Register, "Email Already Exists", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    override fun onResume() {
        super.onResume()
        // Get references to the EditText views in your layout
        val nameEditText = findViewById<EditText>(R.id.register_name)
        val phoneEditText = findViewById<EditText>(R.id.register_phone)
        val emailEditText = findViewById<EditText>(R.id.register_email)
        val passwordEditText = findViewById<EditText>(R.id.register_password)
        val userTypeRadioGroup = findViewById<RadioGroup>(R.id.register_user_type)
        val btnRegister = findViewById<Button>(R.id.register_button)

        btnRegister.setOnClickListener() {

            var validated = true

            //Get values from user input
            val name = nameEditText.text.toString().trim();
            val email = emailEditText.text.toString().trim();
            val phoneNumber = phoneEditText.text.toString().trim();
            val password = passwordEditText.text.toString().trim();

            var userType = ""

            // Get the selected user type from the RadioGroup
            val userTypeRadioButton =
                findViewById<RadioButton>(userTypeRadioGroup.checkedRadioButtonId)

            //Data Validation
            // Validate name
            if (name.isEmpty()) {
                nameEditText.error = "Name is required"
                nameEditText.requestFocus()
                validated = false
            }

            // Validate phone number
            else if (phoneNumber.isEmpty()) {
                phoneEditText.error = "Phone number is required"
                phoneEditText.requestFocus()
                validated = false
            } else if (phoneNumber.length != 10 ) {
                phoneEditText.error = "Please enter a valid phone number"
                phoneEditText.requestFocus()
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

            // Validate password
            else if (password.isEmpty()) {
                passwordEditText.error = "Password is required"
                passwordEditText.requestFocus()
                validated = false

            } else if (password.length < 6) {
                passwordEditText.error = "Password should be at least 6 characters "
                passwordEditText.requestFocus()
                validated = false
            }

            else if(userTypeRadioButton == null){
                validated = false
                Toast.makeText(this, "User Type Required", Toast.LENGTH_SHORT).show()
            } else{
                userType = userTypeRadioButton.text.toString()
            }

            if (validated) {
                //Call registerUser function
                registerUser(name,phoneNumber, userType, email, password)
            }

        }
    }
}