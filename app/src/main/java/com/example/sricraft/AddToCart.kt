package com.example.sricraft

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.models.sricraft.Cart
import com.models.sricraft.CartItem

class AddToCart : AppCompatActivity() {

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
        setContentView(R.layout.activity_add_to_cart)

        //Get item id from intent
        var itemId = intent.getStringExtra("id")
        var title = intent.getStringExtra("title")
        var description = intent.getStringExtra("description")
        var price = intent.getStringExtra("price")
        var image = intent.getStringExtra("imageURL").toString()
        var userID = intent.getStringExtra("userID")

        //Get views
        val titleEditText = findViewById<TextView>(R.id.item_title)
        val priceEditText = findViewById<TextView>(R.id.item_price)
        val descriptionEditText = findViewById<TextView>(R.id.item_description)
        val itemAmount = findViewById<TextView>(R.id.item_amount)
        val plusButton = findViewById<Button>(R.id.plus_button)
        val minusButton = findViewById<Button>(R.id.minus_button)
        val buyButton = findViewById<Button>(R.id.add_to_cart_button)

        //Set values
        titleEditText.setText(title)
        priceEditText.setText(price)
        descriptionEditText.setText(description)

        //Display image
        Glide.with(this@AddToCart).load(image).into(findViewById(R.id.item_image))

        //Increase amount
        var cost = price?.toFloat()
        var amount = 1

        plusButton.setOnClickListener {
            amount = itemAmount.text.toString().toInt()
            amount++
            if (price != null) {
                cost = cost?.plus(price.toFloat())
                priceEditText.setText(String.format("%.2f", cost))
            }
            itemAmount.setText(amount.toString())
        }

        //Decrease amount
        minusButton.setOnClickListener {
            amount = itemAmount.text.toString().toInt()
            if(amount > 1){
                amount--
                itemAmount.setText(amount.toString())
            }
            if (price != null) {
                cost = cost?.minus(price.toFloat())
                priceEditText.setText(String.format("%.2f", cost))
            }
        }

        //Add to cart
        buyButton.setOnClickListener {
            //Add to cart item
            // Retrieve the cart from shared preferences
            val sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE)
            val savedCartJson = sharedPreferences.getString("cart", null)
            val savedCart = Gson().fromJson(savedCartJson, Cart::class.java)

            // Add new items to the cart
            savedCart.add(CartItem(itemId.toString(), title.toString(), price.toString(), image, amount))

            // Save the updated cart to shared preferences
            val updatedCartJson = Gson().toJson(savedCart)
            sharedPreferences.edit().putString("cart", updatedCartJson).apply()
            finish()
        }
    }
}