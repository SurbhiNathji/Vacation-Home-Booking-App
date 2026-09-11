package com.example.pdsproject


/**
* Description: RecyclerView Adapter to bind a list of House objects to views
* defined in item_house.xml layout. Handles displaying title, price, and image,
* and navigates to the house detail view when an item is clicked.
*
* Developed by: Surbhi Nathji
*/


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import com.example.pdsproject.model.House


/**
* Adapter for displaying a list of House objects in a RecyclerView.
*
* @param houses List of houses to be displayed.
* @param ctx Context used for image loading and starting new activities.
* @param activity Activity reference, can be used if needed for callbacks.
*/
class HouseAdapter(
   // private val houses: List<House>,
   private val ctx: Context,
   private val activity: Activity
) : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {


   private val houses = mutableListOf<House>()
   // new function to update data
   fun updateData(newHouses: List<House>) {
       houses.clear()
       houses.addAll(newHouses)
       notifyDataSetChanged()
   }
   /**
    * ViewHolder class representing each individual house item.
    */
   class HouseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
       val houseImage: ImageView = view.findViewById(R.id.houseImage)
       val houseTitle: TextView = view.findViewById(R.id.houseTitle)
       val housePrice: TextView = view.findViewById(R.id.housePrice)

   }


   /**
    * Inflates the layout for each house item.
    */
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_house, parent, false)
       return HouseViewHolder(view)
   }


   /**
    * Binds data to each ViewHolder.
    */
   override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
       val house = houses[position]


       // Load house image using Glide library
       Glide.with(ctx)
           .load(house.imageUrl)
           .into(holder.houseImage)



       // Set house title and price
       holder.houseTitle.text = house.title
       holder.housePrice.text = house.price.toString()



       // Set click listener to navigate to the detail page
       // but now we pass only id and make another api call
       holder.itemView.setOnClickListener {
           val intent = Intent(ctx, HouseDetailActivity::class.java).apply {
               putExtra("HOME_ID", house.id)
           }
           ctx.startActivity(intent)
       }
   }


   /**
    * Returns the total number of items in the list.
    */
   override fun getItemCount() = houses.size
}




