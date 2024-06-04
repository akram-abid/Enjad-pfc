package com.example.depannage.screens.clientHomeScreen

data class BrokenCar (
    val id: String,
    val cID: String,
    val token: String,
    val carType: String,
    val carYear: String,
    val carColor: String,
    val currentLocationLat: Double,
    val currentLocationLong: Double,
    val destinationLocationLat: Double,
    val destinationLocationLong: Double,
    val clientPhoneNumber: String
)