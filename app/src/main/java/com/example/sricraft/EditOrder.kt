package com.example.sricraft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class EditOrder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_order)

        //Set views
        val etTitle = findViewById<TextView>(R.id.order_title)
        val etTotal = findViewById<TextView>(R.id.order_total)
        val atAddress = findViewById<TextView>(R.id.order_address)
        val btnUpdate = findViewById<TextView>(R.id.update_btn)
        val btnDelete = findViewById<TextView>(R.id.delete_btn)

        //Get data from intent
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val total = intent.getStringExtra("total")
        val address = intent.getStringExtra("address")

        Toast.makeText(this, address, Toast.LENGTH_SHORT).show()


        //Set data to views
        if (title != null) {
            etTitle.text = title.trim().dropLast(1)
        }

        etTotal.text = "Rs.$total"
        atAddress.setText(address)

        //Update button click
        btnUpdate.setOnClickListener {
            //Get updated data
            val updatedAddress = atAddress.text.toString()

            //Update data in database
            // Get a reference to the database node you want to update
            val databaseRef = FirebaseDatabase.getInstance().getReference("orders").child(id.toString()).child("address")
            Toast.makeText(this, "Address Updated", Toast.LENGTH_SHORT).show()
            finish()

            // Update the values of the node
            databaseRef.setValue(updatedAddress)
        }

        //Delete button click
        btnDelete.setOnClickListener {
            //Delete data from database
            // Get a reference to the database node you want to update
            val databaseRef = FirebaseDatabase.getInstance().getReference("orders").child(id.toString())
            Toast.makeText(this, "Order Cancelled", Toast.LENGTH_SHORT).show()
            finish()

            // Update the values of the node
            databaseRef.removeValue()
        }

    }
}