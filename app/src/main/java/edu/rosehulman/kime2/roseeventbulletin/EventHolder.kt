package edu.rosehulman.kime2.roseeventbulletin

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class EventHolder(eventView: View, var adapter: ListAdapter): RecyclerView.ViewHolder(eventView) {
    val eventName = eventView.findViewById<TextView>(R.id.event_list_title)
    val eventDate = eventView.findViewById<TextView>(R.id.event_list_date)
    val eventLocation = eventView.findViewById<TextView>(R.id.event_list_location)
    val dataService = DataService()

    init {
        eventView.setOnClickListener {
            adapter.selectEvent(adapterPosition)
        }
    }

    fun bind(event: Event) {
        eventName.text = event.name
        eventDate.text = event.date.toString()
        dataService.getLocationByUID(event.location).addOnSuccessListener { eventLocation.text = it.getString("name") }

    }
}