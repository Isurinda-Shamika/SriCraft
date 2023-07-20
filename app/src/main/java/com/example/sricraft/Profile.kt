package com.example.sricraft

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.models.sricraft.User

class Profile : AppCompatActivity() {

    var name = ""
    var phoneNumber = ""
    var email = ""
    var dp = ""
    var edit = false

    // Get the current user ID
    private val auth = FirebaseAuth.getInstance()
    var currentUserID = auth.currentUser?.uid;

    // Get a reference to the Firebase Realtime Database
    val database = FirebaseDatabase.getInstance().reference
    private val storageRef = Firebase.storage.reference

    // Get a reference to the "users" node in the database
    val usersRef = database.child("users")

    private var imageUri: Uri? = null

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                imageUri = result.data?.data
                //Display image
                Glide.with(this).load(imageUri).circleCrop().into(findViewById(R.id.profile_picture))

            }
            else{
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                imageUri = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Floating action button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

        val nameEditText = findViewById<EditText>(R.id.profile_name)
        val phoneEditText = findViewById<EditText>(R.id.profile_phone)
        val emailEditText = findViewById<EditText>(R.id.profile_email)
        val btnEdit = findViewById<Button>(R.id.edit_profile_button)
        val btnSave = findViewById<Button>(R.id.update_profile_button)
        val profileImage = findViewById<ImageView>(R.id.profile_picture)
        val btnSignOut = findViewById<Button>(R.id.sign_out_button)
        val resetPass = findViewById<TextView>(R.id.reset_password_text)
        val deleteProfile = findViewById<TextView>(R.id.delete_profile)

        //Hide save button
        btnSave.visibility = View.GONE

        //Get data
        // Query the database for the current user's details
        usersRef.child(currentUserID.toString()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the snapshot exists and has children
                if (snapshot.exists() && snapshot.hasChildren()) {
                    // Get the user details from the snapshot
                     name = snapshot.child("name").value.toString()
                     phoneNumber = snapshot.child("phoneNumber").value.toString()
                     email = snapshot.child("email").value.toString()
                     dp = snapshot.child("dp").value.toString()


                    //Display values
                    nameEditText.setText(name)
                    phoneEditText.setText(phoneNumber)
                    emailEditText.setText(email)

                    //Made edit text disabled
                    nameEditText.isEnabled = false
                    phoneEditText.isEnabled = false
                    emailEditText.isEnabled = false

                    //Set profile picture
                    if(dp != ""){
                        Glide.with(this@Profile).load(dp).circleCrop().into(findViewById(R.id.profile_picture))
                    }

                    //Set on click listener
                    btnEdit.setOnClickListener() {
                        //Made edit text enabled
                        nameEditText.isEnabled = true
                        phoneEditText.isEnabled = true

                        //Show save button
                        btnSave.visibility = View.VISIBLE
                        btnEdit.visibility = View.GONE

                        edit = true
                    }

                    //Set on click listener
                    btnSave.setOnClickListener() {
                        edit = false

                        //Get values from user input
                       name = nameEditText.text.toString().trim();
                       email = emailEditText.text.toString().trim();
                       phoneNumber = phoneEditText.text.toString().trim();

                        var validated = true

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

                        if(validated) {
                            if(imageUri != null){
                                val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

                                // Upload the image to Firebase Storage
                                imageRef.putFile(imageUri!!)
                                    .addOnSuccessListener { taskSnapshot ->
                                        // Get the download URL of the uploaded image
                                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                                            UpdateDetails(name, phoneNumber, uri.toString())
                                        }
                                    }
                            }else{
                                UpdateDetails(name, phoneNumber, dp)
                            }

                        }
                    }

                } else {
                   Toast.makeText(this@Profile, "Error getting data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error case
            }
        })

        profileImage.setOnClickListener() {
            if(edit) {
                onProfilePictureClick(profileImage)
            }
        }

        //Set on click listener sign out btn
        btnSignOut.setOnClickListener() {
            FirebaseAuth.getInstance().signOut()
            //Redirect to login page
            Intent(this, Login::class.java).also {
                startActivity(it)
            }
        }

        //Send reset password link firebase auth
        resetPass.setOnClickListener() {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@Profile, "Reset password link sent to your email", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //Delete profile
        deleteProfile.setOnClickListener() {
            //Delete user from firebase auth
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Delete user from database
                        usersRef.child(currentUserID.toString()).removeValue()
                        Toast.makeText(this@Profile, "Profile deleted", Toast.LENGTH_SHORT).show()
                        //Redirect to login page
                        Intent(this, Login::class.java).also {
                            startActivity(it)
                        }
                    }
                }
        }
    }

    fun onProfilePictureClick(view: View) {
        // PICK INTENT picks item from data
        // and returned selected item
        val galleryIntent = Intent(Intent.ACTION_PICK)
        // here item is type of image
        galleryIntent.type = "image/*"
        // ActivityResultLauncher callback
        imagePickerActivityResult.launch(galleryIntent)
    }


    fun UpdateDetails(name: String, phoneNumber: String, dp: String){

        //Update user details
        var ref = usersRef.child(currentUserID.toString())

        val updates = HashMap<String, Any>()
        updates["name"] = name
        updates["phoneNumber"] = phoneNumber
        updates["dp"] = dp

        //Update
        ref.updateChildren(updates)

        Toast.makeText(this@Profile, "Profile Updated", Toast.LENGTH_SHORT)
            .show()

        //Refresh activity
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}