package edu.rosehulman.kime2.roseeventbulletin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.app_bar_main.*

class MapsActivity : Fragment(), OnMapReadyCallback {

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
        Log.d("hillo", "hi")

        val rose = LatLng(39.48, -87.32)
        mMap.addMarker(MarkerOptions().position(rose).title("Marker in Rose-Hulman"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rose, 20f))
    }
}
