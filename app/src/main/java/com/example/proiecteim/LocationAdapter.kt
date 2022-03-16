package com.example.proiecteim

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_location.view.*

class LocationAdapter (
    private val locations: MutableList<Location>
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

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currLocation = locations[position]
        holder.itemView.apply {
            val displayText = currLocation.name + " - " + currLocation.currTemp + "°C"
            locationEntry.text = displayText

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