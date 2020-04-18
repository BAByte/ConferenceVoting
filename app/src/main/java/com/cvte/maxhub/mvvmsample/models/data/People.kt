package com.cvte.maxhub.mvvmsample.models.data


data class People(
    val uid: String, val name: String, val supportVotingId: Int, var longitude: Float = 0f,
    var latitude: Float = 0f
)