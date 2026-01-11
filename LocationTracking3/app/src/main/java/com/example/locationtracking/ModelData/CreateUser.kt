package com.example.locationtracking.ModelData

import com.google.firebase.database.IgnoreExtraProperties


data class CreateUser @JvmOverloads constructor(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var issharing: Boolean? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var city: String? = null,
    var charge: String? = null,
    var network: String? = null,
    var address: String? = null,
    var imageUrl: String? = null,
    var userId: String? = null
)

data class CircleMember(
    val userId: String
)
@IgnoreExtraProperties
data class CircleUser(
    val circleName: String,
    val members: Map<String, CircleMember>
)