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
    fun getEventByUid(uid: String): Task<DocumentSnapshot> {
        return eventsRef
            .document(uid)
            .get()
    }

    fun setAttending(uid: String, eventId: String, isAttending: Boolean) {
        if (isAttending) {
            usersRef.document(uid).get().addOnSuccessListener {
                val user = User.fromSnapShot(it)
                user.attending.add(eventId)
                usersRef.document(uid).set(user)
            }

            eventsRef.document(eventId).get().addOnSuccessListener {
                val event = Event.fromSnapshot(it)
                event.attendees.add(uid)
                eventsRef.document(eventId).set(event)
            }
        } else {
            usersRef.document(uid).get().addOnSuccessListener {
                val user = User.fromSnapShot(it)
                user.attending.remove(eventId)
                usersRef.document(uid).set(user)
            }

            eventsRef.document(eventId).get().addOnSuccessListener {
                val event = Event.fromSnapshot(it)
                event.attendees.remove(uid)
                eventsRef.document(eventId).set(event)
            }
        }
    }

    fun getUserIDByUsername(username: String): Task<QuerySnapshot> {
        return usersRef.whereEqualTo("username", username).get()
    }
}