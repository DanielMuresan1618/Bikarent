package com.example.bikarent.models

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

//not used
data class UserLocation(var user: User?, val geo_point: GeoPoint?, @ServerTimestamp var timestamp: Date? = null)

