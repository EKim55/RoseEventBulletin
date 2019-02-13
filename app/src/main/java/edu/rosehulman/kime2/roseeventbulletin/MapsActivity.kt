package edu.rosehulman.kime2.roseeventbulletin

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.create_edit_event.view.*
import java.util.ArrayList
import java.util.HashMap

class MapsActivity : Fragment(), OnMapReadyCallback {
    private val dataService = DataService()

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.fab.hide()

        var view = inflater.inflate(R.layout.activity_maps, container, false)

        val manager = fragmentManager
        val transaction = manager!!.beginTransaction()
        val fragment = SupportMapFragment()
        transaction.add(R.id.map, fragment)
        transaction.commit()
//        setContentView(R.layout.activity_maps)
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)
        return view
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val locMap = HashMap<String, String>()
        dataService.getAllLocations().addOnSuccessListener {
            for (document in it.documents) {
                val loc = Location.fromSnapShot(document)
                locMap[loc.name] = loc.id
                val marker = LatLng(loc.lat, loc.lng)
                mMap.addMarker(MarkerOptions().position(marker).title(loc.name).snippet(loc.events.size.toString() + " events"))
            }
        }

        mMap.setOnInfoWindowClickListener {
            val locName = it.title
            val locID = locMap[locName]
            dataService.getLocationByUID(locID!!).addOnSuccessListener {
                val ft = activity!!.supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_container, ListFragment.newInstance(locID, true, "location"), getString(R.string.event_list_stack))
                ft.addToBackStack(getString(R.string.event_list_stack))
                ft.commit()
            }
        }
        val rose = LatLng(39.483592, -87.326766)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rose, 19f))
    }
}
