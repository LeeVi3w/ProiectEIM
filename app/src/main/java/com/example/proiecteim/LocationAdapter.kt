package com.example.proiecteim

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_location.view.*

class LocationAdapter (
    private val locations: ArrayList<Location>
    ) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_location,
                parent,
                false
            )
        )
    }

    fun addLocation(location: Location) {
        locations.add(location)
        notifyItemInserted(locations.size - 1)
    }

    fun setLocation(position: Int, location: Location) {
        locations[position] = location
        Log.d("setLocation", location.alertTemp.toString())
        notifyItemChanged(position)
    }

    fun getLocations(): ArrayList<Location> {
        return locations
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currLocation = locations[position]
        Log.d("onBindViewHolder", currLocation.alertTemp.toString())
        holder.itemView.apply {
            val displayText = currLocation.name + " - " + currLocation.currTemp + "°C"
            locationEntry.text = displayText

            locationEntry.setOnClickListener {
                val intent = Intent(this.context, LocationActivity::class.java)
                intent.putExtra("Location", currLocation)
                intent.putExtra("LocationList", locations)
                intent.putExtra("LocationIdx", position)
                Log.d("LocationAdapter", this.context.toString())
                (this.context as MainActivity).startActivityForResult(intent, 1)
            }

            deleteButton.setOnClickListener {
                locations.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, locations.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }
}