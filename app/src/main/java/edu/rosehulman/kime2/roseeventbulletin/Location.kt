package edu.rosehulman.kime2.roseeventbulletin

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Location(var name: String = "location name",
                    var events: List<String> = ArrayList()
){
    @get:Exclude
    var id = ""

    override fun toString(): String {
        return "Name: $name, Events: $events"
    }
    companion object {
        fun fromSnapShot(snapshot: DocumentSnapshot): Location {
            val loc = snapshot.toObject(Location::class.java)!!
            loc.id = snapshot.id
            return loc
        }
    }
}
