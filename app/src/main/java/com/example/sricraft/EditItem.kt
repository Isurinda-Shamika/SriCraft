package com.example.sricraft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.models.sricraft.Item
import java.net.URI

class EditItem : AppCompatActivity() {

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
                Glide.with(this).load(imageUri).into(findViewById(R.id.addItem_image))
                imageUpdate = true

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        //Get item id from intent
        var itemId = intent.getStringExtra("id")
        var title = intent.getStringExtra("title")
        var description = intent.getStringExtra("description")
        var price = intent.getStringExtra("price")
        var image = intent.getStringExtra("imageURL").toString()
        var userID = intent.getStringExtra("userID")

        //Get views
        val titleEditText = findViewById<EditText>(R.id.addItem_name)
        val priceEditText = findViewById<EditText>(R.id.addItem_price)
        val descriptionEditText = findViewById<EditText>(R.id.addItem_description)
        val btnSubmit = findViewById<Button>(R.id.addItem_btn)
        val btnChooseImage = findViewById<Button>(R.id.addItem_choose_image)
        val btnDelete = findViewById<Button>(R.id.delete_btn)

        //Set values
        titleEditText.setText(title)
        priceEditText.setText(price)
        descriptionEditText.setText(description)

        //Display image
        Glide.with(this@EditItem).load(image).into(findViewById(R.id.addItem_image))

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
            title = titleEditText.text.toString()
            price = priceEditText.text.toString()
            description = descriptionEditText.text.toString()

            //Validate data
            var validated = true

            //Validate title
            if(title!!.isEmpty()) {
                titleEditText.error = "Title is required"
                titleEditText.requestFocus()
                validated = false
            }
            //Validate price
            else if(price!!.isEmpty()) {
                priceEditText.error = "Price is required"
                priceEditText.requestFocus()
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
                            val item = Item(
                                itemId.toString(),
                                title!!,
                                price!!,
                                description!!,
                                uri.toString(),
                                userID.toString()
                            )
                            database.child("items").child(itemId.toString()).setValue(item)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Item added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("AddItem", "Item added successfully")
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
                val item = Item(
                    itemId.toString(),
                    title!!,
                    price!!,
                    description!!,
                    image,
                    userID.toString()
                )
                database.child("items").child(itemId.toString()).setValue(item)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Item added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("AddItem", "Item added successfully")
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            this,
                            "Error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("AddItem", "Error: ${error.message}")

                    }
            }
        }

        //Delete item
        btnDelete.setOnClickListener {
            database.child("items").child(itemId.toString()).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Item deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("AddItem", "Item deleted successfully")
                    //Go back
                    finish()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(
                        this,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("AddItem", "Error: ${error.message}")
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                    }
                }
        }
    }
}