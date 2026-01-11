package com.example.locationtracking.ModelData

class CircleJoin {
    var circlememberid: String? = null

    constructor(circlememberid: String?) {
        this.circlememberid = circlememberid
    }

    constructor() {}
}

class Circles(
    val name: String,
    val code: String
)

class MemberData(
    val userid: String,
    val lat: Double,
    val lon: Double,
    val city: String,
    val charge: String,
    val network: String,
    val issharing: Boolean,
    val address: String,
    val email: String,
    val username: String,
    val imgurl: String
)

class MemberID(
    val memberid: String
)

class GroupsName(
    val name: String,
    val code: String
)

class Circledata(
    val circlename: String = "ssss"
)

class AllUserid(
    val userid: String
)