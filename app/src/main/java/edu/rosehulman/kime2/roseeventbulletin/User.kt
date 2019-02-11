package edu.rosehulman.kime2.roseeventbulletin

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class User(var username: String = "username",
                var profile_pic: String = "pic_link",
                var name: String = "user's name",
                var email: String = "email",
                var attending: ArrayList<String> = ArrayList(),
                var owned: ArrayList<String> = ArrayList()) {
    @get:Exclude var id = ""

    companion object {
        fun fromSnapShot(snapshot: DocumentSnapshot): User {
            val user = snapshot.toObject(User::class.java)!!
            user.id = snapshot.id
            return user
        }
    }
}
