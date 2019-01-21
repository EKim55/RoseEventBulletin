package edu.rosehulman.kime2.roseeventbulletin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListAdapter(val context: Context?, var listener: ListFragment.OnEventSelectedListener?): RecyclerView.Adapter<EventHolder>() {

    var events = ArrayList<Event>()

    init {
        events.add(Event())
        events.add(Event("name2"))
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false)
        return EventHolder(view, this)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.bind(events[position])
    }

    fun selectEvent(position: Int) {
        listener?.onEventSelected(events[position])
    }
}