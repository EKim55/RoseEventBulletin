package edu.rosehulman.kime2.roseeventbulletin


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.create_edit_event.*
import kotlinx.android.synthetic.main.create_edit_event.view.*
import layout.Constants
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_UID = "ARG_UID"
private const val ARG_EVENT = "ARG_EVENT"

/**
 * A simple [Fragment] subclass.
 * Use the [EditEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EditEventFragment : Fragment() {
    private var loggedInUser: String? = null
    val eventsRef = FirebaseFirestore.getInstance().collection("events")
    private val dataService = DataService()

    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loggedInUser = it.getString(ARG_UID)
            event = it.getParcelable(ARG_EVENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.create_edit_event, container, false)
        val locMap = HashMap<String, String>()
        val locs = ArrayList<String>()
        dataService.getAllLocations().addOnSuccessListener {
            for (document in it.documents) {
                val loc = Location.fromSnapShot(document)
                locs.add(loc.name)
                locMap[loc.name] = loc.id
            }
            val locAdapter = ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, locs)
            view.event_location_input.adapter = locAdapter
        }

        if (event!!.id != "") { // Editing
            Log.d(Constants.USER_LOG, "EVENTNAME: ${event!!.name}")
            view.event_title_input.setText(event!!.name)
            view.event_description_input.setText(event!!.description)
            view.event_date_input.setText(event!!.date)
            var locIndex = 0
            for (i in locs.indices) {
                if (locs[i] == event!!.location) {
                    locIndex = i
                }
            }
            view.event_location_input.setSelection(locIndex)
        }

        view.submit_button.setOnClickListener {
            // TODO: Add input validation
            if (event!!.id == "") { // New Event
                val event = Event(
                    event_title_input.text.toString(),
                    event_description_input.text.toString(),
                    event_date_input.text.toString(),
                    loggedInUser ?: "",
                    locMap[event_location_input.selectedItem]!!,
                    ArrayList())
                eventsRef.add(event).addOnSuccessListener {
                    Log.d("TESTING", "id " + it.id)
                    event.id = it.id
                    dataService.setAttending(loggedInUser!!, it.id, true)
                    dataService.addEventToLoc(it.id, locMap[event_location_input.selectedItem]!!)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragment_container, EventDetailFragment.newInstance(event, loggedInUser!!), getString(R.string.event_list_stack))
                    ft.commit()
                }
            } else {
                val editedEvent = Event(
                    event_title_input.text.toString(),
                    event_description_input.text.toString(),
                    event_date_input.text.toString(),
                    loggedInUser ?: "",
                    locMap[event_location_input.selectedItem]!!,
                    event!!.attendees
                )
                eventsRef.document(event!!.id).set(editedEvent).addOnSuccessListener {
                    dataService.addEventToLoc(event!!.id, locMap[event_location_input.selectedItem]!!)
                    dataService.removeEventFromLoc(event!!.id, event!!.location)
                    val ft = activity!!.supportFragmentManager.beginTransaction()
                    ft.replace(
                        R.id.fragment_container,
                        EventDetailFragment.newInstance(editedEvent, loggedInUser!!),
                        getString(R.string.event_list_stack)
                    )
                    ft.commit()
                }
            }
        }
        view.cancel_button.setOnClickListener {
            activity!!.onBackPressed()
        }


        activity!!.fab.hide()
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditEventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(uid: String, event: Event = Event()) =
            EditEventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                    putParcelable(ARG_EVENT, event)
                }
            }
    }
}
