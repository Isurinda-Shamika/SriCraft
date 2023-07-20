package com.example.sricraft

import MarketItemAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.models.sricraft.Cart
import com.models.sricraft.CartItem

class Market : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)

        //Floating action button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        
        //Add cart object to shared preferences
        val sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE)
        val cart = Cart()
        val cartJson = Gson().toJson(cart)
        sharedPreferences.edit().putString("cart", cartJson).apply()
    }

    override fun onResume() {
        super.onResume()


        val sharedPreferences = getSharedPreferences("Cart", Context.MODE_PRIVATE)
        val savedCartJson = sharedPreferences.getString("cart", null)
        val savedCart = Gson().fromJson(savedCartJson, Cart::class.java)
        val cartSize = savedCart.getCartItems().size

        if(cartSize > 0) {
            findViewById<TextView>(R.id.cart_count).text = cartSize.toString()

            //Go to checkout
            findViewById<LinearLayout>(R.id.cart_layout).setOnClickListener {
                val checkoutIntent = Intent(this, Checkout::class.java)
                startActivity(checkoutIntent)
            }
        }

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)

        // this creates a vertical layout Manager
        recyclerview.layoutManager =  GridLayoutManager(this, 2)

        // This will pass the ArrayList to our Adapter
        val adapter = MarketItemAdapter()

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
}