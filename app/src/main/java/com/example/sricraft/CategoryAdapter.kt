package com.example.sricraft

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.models.sricraft.Category
import com.models.sricraft.Item

class CategoryAdapter() : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    val data = ArrayList<Category>()

    private lateinit var database: DatabaseReference
    private lateinit var listener: ValueEventListener

    init {
        database = FirebaseDatabase.getInstance().reference.child("categories")
        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").value.toString()
                    val name = snapshot.child("name").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val imageURL = snapshot.child("imageURL").value.toString()

                    //Check userID
                    data.add(Category(id, name, description, imageURL))

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
            .inflate(R.layout.category_view, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Item = data[position]

        //Load image using glide
        Glide.with(holder.itemView.context)
            .load(Item.imageURL).circleCrop()
            .into(holder.imageView)



        // sets the text to the textview from our itemHolder class
        holder.textView.text = Item.name

        holder.card.setOnClickListener(View.OnClickListener {
            //Go to edit item page
            val intent = Intent(holder.itemView.context, EditCategory::class.java)
            intent.putExtra("id", Item.id);
            intent.putExtra("name", Item.name);
            intent.putExtra("description", Item.description);
            intent.putExtra("imageURL", Item.imageURL);
            holder.itemView.context.startActivity(intent);
        })

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return data.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val card: View = itemView.findViewById(R.id.card)
    }
}