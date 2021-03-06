package edu.rosehulman.kime2.roseeventbulletin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import layout.Constants


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_USER = "ARG_USER"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var uid: String? = null
    val dataService = DataService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_USER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.fab.hide()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        dataService.getUserByUid(uid!!).addOnSuccessListener {
            view.full_name.text = it.getString("name")
            view.username.text = it.getString("username")
            view.email.text = it.getString("email")
        }
        view.hosted_events_button.setOnClickListener {
            swapToFilteredList("owner")
        }
        view.attending_events_button.setOnClickListener {
            swapToFilteredList("attendees")
        }
        return view
    }

    private fun swapToFilteredList(filter: String) {
        val ft = activity!!.supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, ListFragment.newInstance(uid!!, true, filter), getString(R.string.event_list_stack))
        ft.addToBackStack(getString(R.string.event_list_stack))
        ft.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(uid: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER, uid)
                }
            }
    }
}
