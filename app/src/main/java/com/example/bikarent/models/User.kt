package com.example.bikarent.models

import android.os.Parcel
import android.os.Parcelable

class User (email: String?,user_id: String?,username: String?,avatar: String?) {
/*
    var email: String? = null
    var user_id: String? = null
    var username: String? = null
    var avatar: String? = null
*/
    constructor(inp: Parcel) : this(email = inp.readString(), user_id=inp.readString(), username = inp.readString(), avatar = inp.readString())


    val CREATOR: Parcelable.Creator<com.example.bikarent.models.User> =
        object : Parcelable.Creator<com.example.bikarent.models.User> {
            override fun createFromParcel(inp: Parcel): com.example.bikarent.models.User? {
                return com.example.bikarent.models.User(inp)
            }

            override fun newArray(size: Int): Array<com.example.bikarent.models.User?> {
                return arrayOfNulls<com.example.bikarent.models.User>(size)
            }
        }

    override fun toString(): String {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}'
    }

    fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(email)
        dest.writeString(user_id)
        dest.writeString(username)
        dest.writeString(avatar)
    }
}

