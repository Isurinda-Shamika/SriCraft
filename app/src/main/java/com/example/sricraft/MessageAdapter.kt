package com.example.sricraft

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sricraft.EditItem
import com.example.sricraft.EditOrder
import com.example.sricraft.R
import com.google.firebase.database.*
import com.models.sricraft.Contact
import com.models.sricraft.Item
import com.models.sricraft.Order

class MessageAdapter() : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val data = ArrayList<Contact>()

    private lateinit var database: DatabaseReference
    private lateinit var listener: ValueEventListener

    init {
        database = FirebaseDatabase.getInstance().reference.child("contactus")
        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").value.toString()
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val message = snapshot.child("message").value.toString()
                    val userId = snapshot.child("userID").value.toString()

                    data.add(Contact(id, name, email, message, userId))
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Log.e("TAG", "onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(listener)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_view, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Item = data[position]


        // sets the text to the textview from our itemHolder class
        holder.txtNmae.text = Item.name
        holder.txtEmail.text = Item.email
        holder.txtMessage.text = Item.message

        holder.btnDelete.setOnClickListener {
            database.child(Item.id).removeValue()
            Toast.makeText(it.context, "Message Deleted", Toast.LENGTH_SHORT).show()
            val intent = Intent(it.context, AllMessages::class.java)
            it.context.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return data.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val txtNmae = itemView.findViewById(R.id.name) as TextView
            val txtEmail = itemView.findViewById(R.id.email) as TextView
            val txtMessage = itemView.findViewById(R.id.message) as TextView
            val btnDelete = itemView.findViewById(R.id.delete) as ImageButton

    }
}
