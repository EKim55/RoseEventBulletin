package edu.rosehulman.kime2.roseeventbulletin

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlin.collections.ArrayList

data class Event(var name: String = "event name",
                 var description: String = "event description",
                 var date: String = "",
                 var owner: String = "",
                 var location: String = "",
                 var attendees: ArrayList<String> = ArrayList()
): Parcelable {
    @get:Exclude var id = ""

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()
    ) {
        id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(owner)
        parcel.writeString(location)
        parcel.writeStringList(attendees)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(document: DocumentSnapshot): Event {
            val event = document.toObject(Event::class.java)!!
            event.id = document.id
            return event
        }
    }
}