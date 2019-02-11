package edu.rosehulman.kime2.roseeventbulletin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.event_details.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.fab.hide()
        val view = inflater.inflate(R.layout.event_details, container, false)
        dataService.getLocationByUID(event!!.location).addOnSuccessListener { view.event_detail_location.text = it.getString("name") }
        dataService.getUserByUid(event!!.owner).addOnSuccessListener {view.event_detail_host.text = it.getString("name") }
        view.event_detail_title.text = event?.name
        view.event_detail_date.text = event?.date.toString()
        view.event_detail_description.text = event?.description
        return view

    }

    companion object {
        @JvmStatic
        fun newInstance(event: Event) =
            EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, event)
                }
            }
    }
}
