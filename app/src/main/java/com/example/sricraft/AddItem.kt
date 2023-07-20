package com.example.sricraft

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.models.sricraft.Item


class AddItem : AppCompatActivity() {

    // Get the current user ID
    val auth = FirebaseAuth.getInstance()
    var currentUserID = auth.currentUser?.uid;

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
                Glide.with(this).load(imageUri).into(findViewById(R.id.addItem_image))

            }
            else{
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                imageUri = null
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        //Floating action button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

        //Get views
        val titleEditText = findViewById<EditText>(R.id.addItem_name)
        val priceEditText = findViewById<EditText>(R.id.addItem_price)
        val descriptionEditText = findViewById<EditText>(R.id.addItem_description)
        val btnSubmit = findViewById<Button>(R.id.addItem_btn)
        val btnChooseImage = findViewById<Button>(R.id.addItem_choose_image)

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
            val title = titleEditText.text.toString()
            val price = priceEditText.text.toString()
            val description = descriptionEditText.text.toString()

            //Validate data
            var validated = true

            //Validate title
            if(title.isEmpty()) {
                titleEditText.error = "Title is required"
                titleEditText.requestFocus()
                validated = false
            }
            //Validate price
            else if(price.isEmpty()) {
                priceEditText.error = "Price is required"
                priceEditText.requestFocus()
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
                            val key: String? = database.child("items").push().key
                            val item = Item(key.toString() , title, price, description, uri.toString(), currentUserID.toString())


                            database.child("items").child(key.toString()).setValue(item)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                                    Log.d("AddItem", "Item added successfully")
                                    Intent(this, MainActivity::class.java).also {
                                        startActivity(it)
                                    }

                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("AddItem", "Error: ${error.message}")
                                    Intent(this, MainActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { error ->
                       Log.e("AddItem", "Error: ${error.message}")
                    }
            }
            else{
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}