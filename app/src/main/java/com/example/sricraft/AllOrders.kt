package com.example.sricraft

import AllOrdersAdapter
import CustomAdapter
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class AllOrders : AppCompatActivity() {

    // Get the current user ID
    private val auth = FirebaseAuth.getInstance()
    var userID = auth.currentUser?.uid;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders)

        //Floating action button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setColorFilter(Color.WHITE);
        fab.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        val adapter = AllOrdersAdapter(userID = userID!!)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
}