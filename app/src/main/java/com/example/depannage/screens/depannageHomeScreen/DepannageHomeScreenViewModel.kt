package com.example.depannage.screens.depannageHomeScreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.depannage.screens.clientHomeScreen.BrokenCar
import com.example.depannage.screens.clientHomeScreen.Profileinfos
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DepannageHomeScreenViewModel @Inject constructor(
    val requestStatus: RequestType,
    val profileInfos: Profileinfos
): ViewModel() {
    val locations = mutableListOf<BrokenCar>()
    val auth = FirebaseAuth.getInstance()
    val dataBase = Firebase.firestore
    val uiState = mutableStateOf(DepannageUiState())

    fun updateProfileInfos(
        username: String,
        email: String,
        phoneNumber: String,
        province: String,
        city: String,
        profilePic: String,
        password: String
    ){
        profileInfos.updateUsername(username)
        profileInfos.updateEmail(email)
        profileInfos.updatePhoneNumber(phoneNumber)
        profileInfos.updateProvinvce(province)
        profileInfos.updatecity(city)
        profileInfos.updateProfilePic(profilePic)
        profileInfos.updatePassword(password)
        profileInfos.updateIsClient(false)
    }
    fun isClientUpdate(variable: Boolean){
        profileInfos.updateIsClient(variable)
    }

    fun updateCurrentLocation(newValue: LatLng){
        uiState.value = uiState.value.copy(currentLocation = newValue)
        requestStatus.updateLocation(newValue)
    }
    fun updateCurrent(location: LatLng){
        uiState.value = uiState.value.copy(currentLocation = location)
    }

    fun updateCarRequestStatus(status: Boolean){
        uiState.value = uiState.value.copy(isCar = status)
        requestStatus.updateCarRequest(status)
    }
    fun updateMarkersList(list: List<BrokenCar>){
        uiState.value = uiState.value.copy(locationsList = list)
    }

    fun updateExcavatorRequestStatus(status: Boolean){
        uiState.value = uiState.value.copy(isExcavator = status)
        requestStatus.updateExcavator(status)
    }

    fun updateClientID(status: String){
        requestStatus.updateClientID(status)
    }
    fun updateInRequest(status: Boolean){
        requestStatus.updateInRequest(status)
    }

    fun checkLocationSetting(
        context: Context,
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit
    ) {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()




        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { onEnabled() }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution)
                        .build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }

    }

    fun updateUserCurrentLocation(
        context: Context,
        settingResultRequest: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onLocationUpdate: (String) -> Unit,
    ) {

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //check if location is enabled

        var isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var locationFine = LatLng(0.0, 0.0)
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val locationService = fusedLocationProviderClient.lastLocation



        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onLocationUpdate("Location permission denied")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                0
            )
            return
        }
        else {
            try {
                if (isLocationEnabled){
                    locationService.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            locationFine = LatLng(task.result.latitude, task.result.longitude)
                            onLocationUpdate("Location: ${locationFine.latitude}, ${locationFine.longitude}")
                            uiState.value = uiState.value.copy(currentLocation = locationFine)
                            updateCurrentLocation(locationFine)
                        } else {
                            onLocationUpdate("Location update failed")
                        }
                    }
                }
                else{
                    onLocationUpdate("enable the location")
                    checkLocationSetting(
                        context = context,
                        onDisabled = { intentSenderRequest ->
                            settingResultRequest.launch(intentSenderRequest)
                        },
                        onEnabled = {}
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("cc", "Error getting location: $e")
                onLocationUpdate("Error getting location")
            }
            updateCurrent(locationFine)

        }
    }
    fun logOut(){
        auth.signOut()
    }
}
