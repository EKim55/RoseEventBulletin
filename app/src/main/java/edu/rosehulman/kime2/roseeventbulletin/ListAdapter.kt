package edu.rosehulman.kime2.roseeventbulletin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*

class ListAdapter(val context: Context?, var listener: ListFragment.OnEventSelectedListener?): RecyclerView.Adapter<EventHolder>() {

    var events = ArrayList<Event>()
    val eventsRef = FirebaseFirestore.getInstance().collection("events")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false)
        return EventHolder(view, this)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun addSnapshotListener() {
        eventsRef
            .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
            if (firebaseFirestoreException != null) {

            } else {
                processSnapshotData(querySnapshot!!)
            }
        }
    }

    fun addEvent(event: Event) {
        eventsRef.add(event)
    }

    fun processSnapshotData(querySnapshot: QuerySnapshot) {
        for (documentChange in querySnapshot.documentChanges) {
            val event = Event.fromSnapshot(documentChange.document)
            when(documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    events.add(0, event)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    for ((k, ev) in events.withIndex()) {
                        if (ev.id == event.id) {
                            events.removeAt(k)
                            notifyItemRemoved(k)
                            break
                        }
                    }
                }
                DocumentChange.Type.MODIFIED -> {
                    for ((k, ev) in events.withIndex()) {
                        if (ev.id == event.id) {
                            events[k] = event
                            notifyItemChanged(k)
                            break
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.bind(events[position])
    }

    fun selectEvent(position: Int) {
        listener?.onEventSelected(events[position])
    }
}