package com.example.sricraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Views
        val btnMarket = findViewById<ImageView>(R.id.market)
        val btnOrders = findViewById<ImageView>(R.id.orders)
        val btnProfile = findViewById<ImageView>(R.id.profile)
        val btnCategories = findViewById<ImageView>(R.id.categories)
        val btnAddItem = findViewById<ImageView>(R.id.addProduct)
        val btnContactus = findViewById<ImageView>(R.id.contactUs)
        val btnEditItem = findViewById<ImageView>(R.id.editItem)
        val btnMessages = findViewById<ImageView>(R.id.allMessages)


        //Listeners
        btnMarket.setOnClickListener {
            val intent = Intent(this, Market::class.java)
            startActivity(intent)
        }

        btnOrders.setOnClickListener {
            val intent = Intent(this, AllOrders::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        btnCategories.setOnClickListener {
            val intent = Intent(this, Categories::class.java)
            startActivity(intent)
        }

        btnAddItem.setOnClickListener {
            val intent = Intent(this, AddItem::class.java)
            startActivity(intent)
        }

        btnContactus.setOnClickListener {
            val intent = Intent(this, ContactUs::class.java)
            startActivity(intent)
        }

        btnEditItem.setOnClickListener {
            val intent = Intent(this, AllItems::class.java)
            startActivity(intent)
        }

        btnMessages.setOnClickListener {
            val intent = Intent(this, AllMessages::class.java)
            startActivity(intent)
        }
    }
}