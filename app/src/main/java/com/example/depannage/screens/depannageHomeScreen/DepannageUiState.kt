package com.example.depannage.screens.depannageHomeScreen

import com.example.depannage.screens.clientHomeScreen.BrokenCar
import com.google.android.gms.maps.model.LatLng

data class DepannageUiState (
    val currentLocation: LatLng? = null,
    val destination: LatLng? = null,
    var carType: String = "ee",
    var carYear: String = "",
    var carColor: String="",
    var isExcavator: Boolean = false,
    var isCar: Boolean = false,
    var locationsList: List<BrokenCar>? = null,
    var token: String = ""
)