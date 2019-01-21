package edu.rosehulman.kime2.roseeventbulletin

import android.os.Parcel
import android.os.Parcelable

data class User(var username: String = "username",
                var profile_pic: String = "pic_link",
                var name: String = "user's name",
                var attending: List<Event> = ArrayList(),
                var owned: List<Event> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Event),
        parcel.createTypedArrayList(Event)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(profile_pic)
        parcel.writeString(name)
        parcel.writeTypedList(attending)
        parcel.writeTypedList(owned)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
