package com.example.sricraft

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sricraft.EditItem
import com.example.sricraft.R
import com.google.firebase.database.*
import com.models.sricraft.Cart
import com.models.sricraft.CartItem
import com.models.sricraft.Item

class CartItemAdapter(cartClass: Cart) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    val data = ArrayList<CartItem>()

    private lateinit var database: DatabaseReference
    private lateinit var listener: ValueEventListener

    //Get items from Shared Preferences

    init{
        for (item in cartClass.getCartItems()) {
            data.add(item)
        }
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Item = data[position]

        //Load image using glide
        Glide.with(holder.itemView.context)
            .load(Item.imageURL)
            .into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.titleView.text = Item.title
        holder.priceView.text = "Rs." + Item.price
        holder.amountVuew.text = "Quantity: " + Item.quantity.toString()

        holder.card.setOnClickListener(View.OnClickListener {
            //Go to edit item page
            val intent = Intent(holder.itemView.context, EditItem::class.java)
            intent.putExtra("id", Item.id);
            intent.putExtra("title", Item.title);
            intent.putExtra("price", Item.price);
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
        val titleView: TextView = itemView.findViewById(R.id.title)
        val priceView: TextView = itemView.findViewById(R.id.price)
        val amountVuew : TextView = itemView.findViewById(R.id.amount)
        val card: View = itemView.findViewById(R.id.card)
    }
}
