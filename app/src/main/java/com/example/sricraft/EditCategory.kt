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

class EditCategory : AppCompatActivity() {
    //Firebase Storage
    private val storageRef = Firebase.storage.reference
    private val database = Firebase.database.reference
    private var imageUri: Uri? = null
    var imageUpdate: Boolean? = false

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                imageUri = result.data?.data

                //Display image
                Glide.with(this).load(imageUri).into(findViewById(R.id.category_image))
                imageUpdate = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        //Get item id from intent
        var itemId = intent.getStringExtra("id")
        var catName = intent.getStringExtra("name")
        var description = intent.getStringExtra("description")
        var image = intent.getStringExtra("imageURL").toString()

        //Get views
        val nameEditText = findViewById<EditText>(R.id.category_name)
        val descriptionEditText = findViewById<EditText>(R.id.category_description)
        val btnSubmit = findViewById<Button>(R.id.category_btn)
        val btnChooseImage = findViewById<Button>(R.id.category_choose_image)
        val btnDelete = findViewById<Button>(R.id.delete_category_btn)

        //Set values
        nameEditText.setText(catName)
        descriptionEditText.setText(description)

        //Display image
        Glide.with(this@EditCategory).load(image).into(findViewById(R.id.category_image))

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
            catName = nameEditText.text.toString()
            description = descriptionEditText.text.toString()

            //Validate data
            var validated = true

            //Validate name
            if(catName!!.isEmpty()) {
                nameEditText.error = "Name is required"
                nameEditText.requestFocus()
                validated = false
            }

            //Validate description
            else if(description!!.isEmpty()) {
                descriptionEditText.error = "Description is required"
                descriptionEditText.requestFocus()
                validated = false
            }

            //If all data is valid, send data to server
            if(validated && imageUpdate == true) {

                // Get a reference to the Firebase Storage where the image will be uploaded
                val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

                // Upload the image to Firebase Storage
                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Get the download URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Save the download URL to Firebase Realtime Database
                            //Get key
                            val item = Category(
                                itemId.toString(),
                                catName!!,
                                description!!,
                                uri.toString()
                            )
                            database.child("categories").child(itemId.toString()).setValue(item)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Category added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("category", "Category added successfully")
                                    Intent(this, MainActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        this,
                                        "Error: ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("category", "Error: ${error.message}")
                                    Intent(this, MainActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.e("category", "Error: ${error.message}")
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                        }
                    }
            }
            else{
                val item = Category(
                    itemId.toString(),
                    catName!!,
                    description!!,
                    image,
                )
                database.child("categories").child(itemId.toString()).setValue(item)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Category updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("category", "Category updated successfully")
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            this,
                            "Error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("category", "Error: ${error.message}")

                    }
            }
        }

        //Delete item
        btnDelete.setOnClickListener {
            database.child("categories").child(itemId.toString()).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Category deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("category", "Category deleted successfully")
                    //Go back
                    finish()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(
                        this,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("category", "Error: ${error.message}")

                }
        }
    }
}