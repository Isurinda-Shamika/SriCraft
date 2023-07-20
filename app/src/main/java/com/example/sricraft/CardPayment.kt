package com.example.sricraft

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.models.sricraft.Cart
import com.models.sricraft.Order
import com.google.firebase.auth.FirebaseAuth

class CardPayment : AppCompatActivity() {

    // Get the current user ID
    private val auth = FirebaseAuth.getInstance()
    var userID = auth.currentUser?.uid;

    //Firebase Storage
    private val storageRef = Firebase.storage.reference
    private val database = Firebase.database.reference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_payment)

        //Get total from intent
        val total = intent.getStringExtra("total")

        //Initialize views
        val cardNumber = findViewById<EditText>(R.id.et_card_number)
        val expieryMM = findViewById<EditText>(R.id.et_expiration_month)
        val expieryYY = findViewById<EditText>(R.id.et_expiration_year)
        val cardCvv = findViewById<EditText>(R.id.et_cvv)
        val etAddress = findViewById<EditText>(R.id.et_Address)
        val btnPay = findViewById<Button>(R.id.btn_pay)
        val btnBack = findViewById<Button>(R.id.btn_cancel)

        btnPay.setOnClickListener(){
            //Get user input
            val cardNum = cardNumber.text.toString()
            val MM = expieryMM.text.toString()
            val YY = expieryYY.text.toString()
            val Cvv = cardCvv.text.toString()
            val address = etAddress.text.toString()

            //Validate data
            var validated = true

            // Validate card number
            if (cardNum.isEmpty()) {
                cardNumber.error = "Card number is required"
                cardNumber.requestFocus()
                validated = false
            }

            // Validate expiration month
            else if (MM.isEmpty()) {
                expieryMM.error = "Expiration month is required"
                expieryMM.requestFocus()
                validated = false

            } else if (MM.length != 2 || MM.toInt() > 12) {
                expieryMM.error = "Please enter a valid expiration month"
                expieryMM.requestFocus()
                validated = false
            }

            // Validate expiration year
            else if (YY.isEmpty()) {
                expieryYY.error = "Expiration year is required"
                expieryYY.requestFocus()
                validated = false

            } else if (YY.length != 2 || YY.toInt() < 23) {
                expieryYY.error = "Please enter a valid expiration year"
                expieryYY.requestFocus()
                validated = false
            }

            // Validate cvv
            else if (Cvv.isEmpty()) {
                cardCvv.error = "CVV is required"
                cardCvv.requestFocus()
                validated = false
            } else if (Cvv.length != 3) {
                cardCvv.error = "Please enter a valid CVV"
                cardCvv.requestFocus()
                validated = false
            }

            // Validate address
            else if (address.isEmpty()) {
                etAddress.error = "Address is required"
                etAddress.requestFocus()
                validated = false
            }

            // If all data is valid
            if (validated) {

                //Create dictionary
                var itemList = mutableMapOf<String, String>()

                //Get cart size
                val sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE)
                val savedCartJson = sharedPreferences.getString("cart", null)
                val savedCart = Gson().fromJson(savedCartJson, Cart::class.java)

                var title = ""
                var orderImage = ""

                for (item in savedCart.getCartItems()) {
                    itemList[item.id] = item.quantity.toString()
                    title += item.title + ", "
                    if(orderImage.isEmpty()){
                        orderImage = item.imageURL.toString()
                    }
                }

                //Save to database
                val key: String? = database.child("orders").push().key
                val item = userID?.let { it1 ->
                    Order(
                        key.toString(),
                        title,
                        itemList,
                        total.toString(),
                        it1,
                        address,
                        orderImage
                    )
                }


                database.child("orders").child(key.toString()).setValue(item)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
                        Log.d("AddOrder", "Order added successfully")
                        //Clear cart
                        val cart = Cart()
                        Toast.makeText(this, "Order success", Toast.LENGTH_LONG).show();
                        val intent =  Intent(this, Feedback::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e("AddOrder", "Error: ${error.message}")
                        //Clear cart
                        val cart = Cart()
                        Toast.makeText(this, "Order success", Toast.LENGTH_LONG).show();
                        val intent =  Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
            }

        }
        btnBack.setOnClickListener(){
           finish()
        }
    }
}