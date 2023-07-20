package com.example.sricraft

import CustomAdapter
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.models.sricraft.Item

class AllItems : AppCompatActivity() {

    //Fetch data from firebase items table
    lateinit var database: DatabaseReference
    lateinit var listener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_items)

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
        val adapter = CustomAdapter();

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
}