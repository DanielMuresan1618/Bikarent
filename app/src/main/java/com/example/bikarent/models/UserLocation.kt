package com.example.bikarent.models

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class UserLocation(var user: User?, val geo_point: GeoPoint?, @ServerTimestamp var timestamp: Date? = null) {
    override fun toString(): String {
        return "UserLocation{" +
                "user=" + user +
                ", geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                '}'
    }
}