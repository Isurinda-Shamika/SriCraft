package com.example.sricraft

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.models.sricraft.Category
import com.models.sricraft.Item

class AddCategory : AppCompatActivity() {
    //Firebase Storage
    private val storageRef = Firebase.storage.reference
    private val database = Firebase.database.reference
    private var imageUri: Uri? = null

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                imageUri = result.data?.data

                //Display image
                Glide.with(this).load(imageUri).circleCrop().into(findViewById(R.id.category_image))

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        //Get views
        val nameEditText = findViewById<EditText>(R.id.category_name)
        val descriptionEditText = findViewById<EditText>(R.id.category_description)
        val btnSubmit = findViewById<Button>(R.id.category_btn)
        val btnChooseImage = findViewById<Button>(R.id.category_choose_image)

        //Get user ID
        val userID = intent.getStringExtra("userID")

        //Select Image
        btnChooseImage.setOnClickListener {
            // PICK INTENT picks item from data
            // and returned selected item
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)
        }

        btnSubmit.setOnClickListener {
            //Get user input
            val catName = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()

            //Validate data
            var validated = true

            //Validate title
            if(catName.isEmpty()) {
                nameEditText.error = "Name is required"
                nameEditText.requestFocus()
                validated = false
            }

            //Validate description
            else if(description.isEmpty()) {
                descriptionEditText.error = "Description is required"
                descriptionEditText.requestFocus()
                validated = false
            }

            //If all data is valid, send data to server
            if(validated && imageUri != null) {

                // Get a reference to the Firebase Storage where the image will be uploaded
                val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

                // Upload the image to Firebase Storage
                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Get the download URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Save the download URL to Firebase Realtime Database
                            //Get key
                            val key: String? = database.child("categories").push().key
                            val item = Category(key.toString() , catName, description, uri.toString())


                            database.child("categories").child(key.toString()).setValue(item)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                                    Log.d("AddCategory", "Category added successfully")
                                    var intent = Intent(this, Categories::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("AddCategory", "Error: ${error.message}")
                                    Intent(this, MainActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.e("AddCategory", "Error: ${error.message}")
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
            }
            else{
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}