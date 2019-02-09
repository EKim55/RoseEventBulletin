package edu.rosehulman.kime2.roseeventbulletin


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_UID = "ARG_UID"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ListFragment : Fragment() {

    var adapter: ListAdapter? = null
    private var listener: OnEventSelectedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // TODO: Do stuff with variables
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.fab.show()
        val view = inflater.inflate(R.layout.event_list, container, false) as RecyclerView
        adapter = ListAdapter(activity, listener)
        adapter!!.addSnapshotListener()
        view.setHasFixedSize(true)
        view.layoutManager = LinearLayoutManager(activity)
        view.adapter = adapter
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnEventSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("Issue in onAttach")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnEventSelectedListener {
        fun onEventSelected(event: Event)
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }
}

