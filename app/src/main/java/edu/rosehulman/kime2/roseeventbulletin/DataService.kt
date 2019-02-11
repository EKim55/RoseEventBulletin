package edu.rosehulman.kime2.roseeventbulletin

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class DataService {
    private val usersRef = FirebaseFirestore.getInstance().collection("users")
    private val eventsRef = FirebaseFirestore.getInstance().collection("events")
    private val locationsRef = FirebaseFirestore.getInstance().collection("locations")

    fun getAllLocations(): Task<QuerySnapshot> {
        return locationsRef
            .get()
    }

    fun getLocationByUID(uid: String): Task<DocumentSnapshot> {
        return locationsRef
            .document(uid)
            .get()
    }

    fun getUserByUid(uid: String): Task<DocumentSnapshot> {
        return usersRef
            .document(uid)
            .get()
    }
}