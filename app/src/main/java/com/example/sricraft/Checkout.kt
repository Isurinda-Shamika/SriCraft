package com.example.sricraft

import MarketItemAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.models.sricraft.Cart

class Checkout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        //Get cart size
        val sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE)
        val savedCartJson = sharedPreferences.getString("cart", null)
        val savedCart = Gson().fromJson(savedCartJson, Cart::class.java)
        val cartSize = savedCart.getCartItems().size

        if(cartSize > 0) {
          Log.d("CART", savedCart.toString())
        }

        //Calculate total
        var total = 0.0
        for(item in savedCart.getCartItems()) {
            total += item.price.toFloat() * item.quantity
        }

        //Set total
        val stotal = String.format("%.2f", total)
        val finalTolal = String.format("%.2f", total + 1500)
        findViewById<TextView>(R.id.subtotal).text = "Rs. $stotal"
        findViewById<TextView>(R.id.total).text = "Rs. $finalTolal"

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        val adapter = CartItemAdapter(savedCart)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        //Go to payment
        findViewById<TextView>(R.id.place_order).setOnClickListener {
            val paymentIntent = Intent(this, CardPayment::class.java)
            //Send total to payment activity
            paymentIntent.putExtra("total", finalTolal)
            startActivity(paymentIntent)
        }
    }
}