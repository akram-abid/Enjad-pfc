package com.example.depannage.screens.clientHomeScreen


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.example.depannage.MainViewModel
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class DriverHomeViewModel @Inject constructor(
    val Location: GlobalData,
    private val auth: FirebaseAuth,
    val serviceType: ServiceType,
    val profileInfos: Profileinfos
): MainViewModel() {

    val lastLocation = Location.location
    val dataBase = Firebase.firestore
    val isLocationEnable = MutableStateFlow(false)

    val uiState = mutableStateOf(DriverHomeUiState())
    var isCircularIndicatorVisible = mutableStateOf(false)
    fun uploadCarteGrisseImage(
        imageUri: Uri,
        context: Context,
        callback: (success: Boolean) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef: StorageReference


        // Generate a unique filename using UUID
        val filename = UUID.randomUUID().toString() + ".jpg"

        imageRef = storageRef.child("images/$filename")

        imageRef.putFile(imageUri)
            .addOnCompleteListener { task ->
                //onCarteGrisseImageChange(imageRef.downloadUrl.result.toString())
                //textTepm = imageRef.downloadUrl.result.toString()
                imageRef.downloadUrl.addOnCompleteListener { task->
                    if (task.isSuccessful){
                        profileInfos.updateProfilePic(task.result.toString())
                        Toast.makeText(context, "hoaw ${task.result}", Toast.LENGTH_SHORT).show()
                        Toast.makeText(context, "hoaw ${profileInfos.profilePic.value}", Toast.LENGTH_SHORT).show()
                        profileInfos.updateProfilePic(task.result.toString())
                        callback(true)
                        // Potentially handle success, e.g., show success toast
                        Toast.makeText(context, "Carte Grise uploaded successfully", Toast.LENGTH_SHORT).show()
                        dataBase.collection("users").document(auth.currentUser?.uid.toString())
                            .update("profile pic", task.result)
                            .addOnCompleteListener {taskk->
                                if (taskk.isSuccessful){
                                    Toast.makeText(context, "pic updated successfully", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "pic updated failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        callback(false)
                        // Handle upload failure gracefully
                     Toast.makeText(context, "Carte Grise upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
             }
        }

    fun isClientUpdate(variable: Boolean){
        profileInfos.updateIsClient(variable)
    }
    fun onCarTypeChange(newValue: String) {
        uiState.value = uiState.value.copy(carType = newValue)
        Location.updateType(newValue)
    }
    fun onCarYearChange(newValue: String) {
        uiState.value = uiState.value.copy(carYear = newValue)
        Location.updateYear(newValue)
    }
    fun onCarColorChange(newValue: String) {
        uiState.value = uiState.value.copy(carColor = newValue)
        Location.updateColor(newValue)
    }

    fun onTokenChange(newValue: String) {
        uiState.value = uiState.value.copy(carColor = newValue)
        Location.updateToken(newValue)
    }

    fun onServiceTypeChanged(newValue: String){
        uiState.value = uiState.value.copy(serviceType = newValue)
        serviceType.updateService(newValue)
    }

    fun logout() {
        auth.signOut()
    }

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
    }

    fun updateUserCurrentLocation(
        context: Context,
        settingResultRequest:ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
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
                            Location.update(locationFine)
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
    fun updateCurrent(location: LatLng){
        uiState.value = uiState.value.copy(currentLocation = location)
    }

    fun updateDestination(location: LatLng){
        uiState.value = uiState.value.copy(destination = location)
        Location.updatedestiantion(location)
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

    fun UseFindRoute(context: Context){
        if (uiState.value.currentLocation != null || uiState.value.destination != null) {
            viewModelScope.launch {
                val route = findRoute(uiState.value.currentLocation!!, LatLng(36.432334, 3.7678)!!)
                if (route != null){
                    Log.d("ccc", "Route found: $route")
                    Toast.makeText(context, "Route found: $route", Toast.LENGTH_LONG).show()
                }else{
                    Log.d("ccc", "its nullllllllllllllllllllllllllllllllllllllllll")
                    Toast.makeText(context, "null", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(context, "not even go inside", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun findRoute(origin: LatLng, destination: LatLng): DirectionsResult? {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=AIzaSyCk41iDC5nl9S8TarE-nhQCjrlFqSBBDhQ" + // Replace with your Google Maps API key AIzaSyCk41iDC5nl9S8TarE-nhQCjrlFqSBBDhQ
                "&travelMode=driving" +
                "&transitRoutingPreference=less_driving"
        val response = withContext(Dispatchers.IO) {
            URL(url).readText()
        }
        return try {
            val gson = Gson()
            gson.fromJson(response, DirectionsResult::class.java)
        } catch (e: Exception) {
            null
        }
    }

    data class DirectionsResult(val routes: List<Route>)
    data class Route(val legs: List<Leg>)
    data class Leg(val polyline: Polyline)
    data class Polyline(val points: String) // Encoded polyline string

}

