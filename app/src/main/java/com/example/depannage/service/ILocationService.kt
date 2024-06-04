package com.example.depannage.service

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface ILocationService {
    fun requestLocationUpdate(): Flow<LatLng?>
    fun requestCurrentLocation(): Flow<LatLng?>
}

class LocationService @Inject constructor(
    private val context: Context,
    private val loactionClient: FusedLocationProviderClient
    ): ILocationService {
    @SuppressLint("MissingPermission")
    //@RequiresApi(Build.VERSION_CODES.S)
    override fun requestLocationUpdate(): Flow<LatLng?> = callbackFlow {
    }

    override fun requestCurrentLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }
}