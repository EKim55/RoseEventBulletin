package edu.rosehulman.kime2.roseeventbulletin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.event_details.*
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
    // TODO: Rename and change types of parameters
    private var event: Event? = null
    private val dataService = DataService()
    private var uid: String? = null
    private var attending = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getParcelable(ARG_PARAM1)
            uid = it.getString(ARG_USER)
        }
        if (event?.attendees!!.contains(uid)) {
            attending = true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                return true
            }
            R.id.action_delete -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.fab.hide()
        if (uid == event!!.owner) {
            val activ = activity!! as MainActivity
            activ.setMenuVisible(true)
        }
        val view = inflater.inflate(R.layout.event_details, container, false)
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
