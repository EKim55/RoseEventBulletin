package edu.rosehulman.kime2.roseeventbulletin

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlin.collections.ArrayList

data class Event(var name: String = "event name",
                 var description: String = "event description",
                 var date: Long = 1,
                 var owner: User = User(),
                 var location: Location = Location(),
                 var attendees: List<User> = ArrayList(),
                 var tags: List<String> = ArrayList()
): Parcelable {
    @get:Exclude var id = ""
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.createTypedArrayList(User),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeLong(date)
        parcel.writeParcelable(owner, flags)
        parcel.writeParcelable(location, flags)
        parcel.writeTypedList(attendees)
        parcel.writeStringList(tags)
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

        fun fromSnapshot(document: QueryDocumentSnapshot): Event {
            val event = document.toObject(Event::class.java)
            event.id = document.id
            return event
        }
    }
}