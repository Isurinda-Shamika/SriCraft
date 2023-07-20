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
import com.example.sricraft.EditOrder
import com.example.sricraft.R
import com.google.firebase.database.*
import com.models.sricraft.Item
import com.models.sricraft.Order

class AllOrdersAdapter(userID:String) : RecyclerView.Adapter<AllOrdersAdapter.ViewHolder>() {

    val data = ArrayList<Order>()

    private lateinit var database: DatabaseReference
    private lateinit var listener: ValueEventListener

    init {
        database = FirebaseDatabase.getInstance().reference.child("orders")
        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val items = snapshot.child("items").value
                    val address = snapshot.child("address").value.toString()
                    val imageURL = snapshot.child("orderImage").value.toString()
                    val total = snapshot.child("total").value.toString()
                    val userId = snapshot.child("userID").value.toString()

                    //Check userID
                    if(userId == userID) {
                        data.add(Order(id, title,
                            items as MutableMap<String, String>, total, userId, address, imageURL))
                    }
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
            .inflate(R.layout.all_order_card_view, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Item = data[position]

        //Load image using glide
        Glide.with(holder.itemView.context)
            .load(Item.orderImage).circleCrop()
            .into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = Item.title.trim().dropLast(1)

        holder.card.setOnClickListener(View.OnClickListener {
            //Go to edit item page
            val intent = Intent(holder.itemView.context, EditOrder::class.java)
            intent.putExtra("id", Item.id);
            intent.putExtra("title", Item.title);
            intent.putExtra("total", Item.total);
            intent.putExtra("imageURL", Item.orderImage);
            intent.putExtra("userID", Item.userID);
            intent.putExtra("address", Item.address);
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
