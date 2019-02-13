package edu.rosehulman.kime2.roseeventbulletin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.event_details.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_USER = "ARG_USER"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EventDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EventDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EventDetailFragment : Fragment() {
    private var event: Event? = null
    private val dataService = DataService()
    private var uid: String? = null
    private var attending = false


    private val eventsRef = FirebaseFirestore.getInstance().collection("events")
    private val usersRef = FirebaseFirestore.getInstance().collection("users")
    private val locRef = FirebaseFirestore.getInstance().collection("locations")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            event = it.getParcelable(ARG_PARAM1)
            uid = it.getString(ARG_USER)
        }
        if (event?.attendees!!.contains(uid)) {
            attending = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("TESTING","deleting")
        return when (item.itemId) {
            R.id.action_edit -> {
                true
            }
            R.id.action_delete -> {
                // DONE: Delete from Events collection
                eventsRef.document(event!!.id).delete()
                // DONE: Delete from Users collection Owned
                // DONE: Delete from all user's attending lists
                usersRef.get().addOnSuccessListener {
                    for (document in it.documents) {
                        var user = User.fromSnapShot(document)
                        user.attending.remove(uid!!)
                        user.owned.remove(event!!.id)
                        usersRef.document().set(user)
                    }
                }
                // DONE: Delete from Locations collection events list
                locRef.document(event!!.location).get().addOnSuccessListener {
                    val loc = Location.fromSnapShot(it)
                    loc.events.remove(event!!.id)
                    locRef.document(event!!.location).set(loc)
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.fab.hide()
        val view = inflater.inflate(R.layout.event_details, container, false)
        if (uid != event!!.owner) {
            view.delete_button.visibility = View.GONE
            view.edit_button.visibility = View.GONE
        } else {
            view.delete_button.setOnClickListener {
                Log.d("TESTING", "EVENT ID IS ${event!!.id}")
                // DONE: Delete from Events collection
                eventsRef.document(event!!.id).delete()
                // DONE: Delete from Users collection Owned
                // DONE: Delete from all user's attending lists
                usersRef.get().addOnSuccessListener {
                    for (document in it.documents) {
                        var user = User.fromSnapShot(document)
                        var edited = false
                        if (user.attending.contains(event!!.id)) {
                            user.attending.remove(event!!.id!!)
                            edited = true
                        }
                        if (user.owned.contains(event!!.id)) {
                            user.owned.remove(event!!.id)
                            edited = true
                        }
                        if (edited) {
                            usersRef.document(user.id).set(user)
                        }
                    }
                }
                // DONE: Delete from Locations collection events list
                locRef.document(event!!.location).get().addOnSuccessListener {
                    val loc = Location.fromSnapShot(it)
                    loc.events.remove(event!!.id)
                    locRef.document(event!!.location).set(loc)
                }
                activity!!.onBackPressed()
            }
            view.edit_button.setOnClickListener {
                val ft = activity!!.supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, EditEventFragment.newInstance(uid!!, event!!), getString(R.string.event_list_stack))
                ft.addToBackStack("create")
                ft.commit()
            }
        }


        view.attending_fab.setOnClickListener {
            this.attending = !this.attending
            if (this.attending) {
                view.attending_fab.setImageResource(R.drawable.ic_menu_send)
                dataService.setAttending(uid!!, event!!.id, true)
            } else {
                view.attending_fab.setImageResource(R.drawable.abc_ic_menu_copy_mtrl_am_alpha)
                dataService.setAttending(uid!!, event!!.id,false)
            }
        }

        dataService.getLocationByUID(event!!.location).addOnSuccessListener { view.event_detail_location.text = it.getString("name") }
        dataService.getUserByUid(event!!.owner).addOnSuccessListener {view.event_detail_host.text = it.getString("name") }
        if (this.attending) {
            view.attending_fab.setImageResource(R.drawable.ic_menu_send)
        } else {
            view.attending_fab.setImageResource(R.drawable.abc_ic_menu_copy_mtrl_am_alpha)
        }
        view.event_detail_title.text = event?.name
        view.event_detail_date.text = event?.date.toString()
        view.event_detail_description.text = event?.description
        return view
    }

    fun updateIcon() {

    }

    companion object {
        @JvmStatic
        fun newInstance(event: Event, uid: String) =
            EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, event)
                    putString(ARG_USER, uid)
                }
            }
    }
}
