package com.example.depannage.screens.depannageHomeScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.depannage.R
import com.example.depannage.screens.clientHomeScreen.BrokenCar
import com.example.depannage.screens.clientHomeScreen.DEFAULT_POS
import com.example.depannage.screens.clientHomeScreen.DriverHomeTopAppBar
import com.example.woof.ui.theme.Shapes
import com.example.woof.ui.theme.Typography
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun FindWorkScreen(
    cancelRequest: ()-> Unit,
    confirmRequest:()-> Unit,
    viewModel: DepannageHomeScreenViewModel
){

    Scaffold(
        modifier = Modifier,
    ) {
        FindWorkElements(
            cancelRequest = cancelRequest,
            confirmRequest = confirmRequest,
            modifier = Modifier.padding(it),
            viewModel = viewModel
        )
    }
}


@Composable
fun FindWorkElements(
    cancelRequest: ()-> Unit,
    confirmRequest: ()-> Unit,
    viewModel: DepannageHomeScreenViewModel,
    modifier : Modifier,
){
    val db = Firebase.firestore
    val defaultCameraPositionState = CameraPosition.fromLatLngZoom(viewModel.requestStatus.location.value, 12f)
    var hasLocationPermission by remember {
        mutableStateOf(false)
    }
    var markLocations by remember { mutableStateOf<List<BrokenCar>?>(null) }
    val locations = mutableListOf<BrokenCar>()
    val locationTowTruck = rememberMarkerState(position = viewModel.requestStatus.location.value)
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    val database = Firebase.database

    val messaging = FirebaseMessaging.getInstance()

    var destination = rememberMarkerState(position = DEFAULT_POS)
    var documentID by remember{ mutableStateOf("") }
    var clientID by remember{ mutableStateOf("") }
    var clientPhoneNumber by remember{ mutableStateOf("") }
    var carType by remember{ mutableStateOf("") }
    var token by remember{ mutableStateOf("") }
    var carYear by remember{ mutableStateOf("") }
    var carColor by remember{ mutableStateOf("") }
    var currentSelectionLocation = rememberMarkerState(position = DEFAULT_POS)
    var currentSelectionDestination = rememberMarkerState(position = DEFAULT_POS)

    val myRef = database.getReference(clientID)

    val context  = LocalContext.current
    val mapUiSettings by remember {
        mutableStateOf(MapUiSettings(compassEnabled = true, myLocationButtonEnabled = true, mapToolbarEnabled = true))
    }

    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val cameraPositionState = rememberCameraPositionState{
        position = defaultCameraPositionState
    }
    var isLoaded by remember {
        mutableStateOf(false)
    }
    var confirmButtonEnabled by remember {
        mutableStateOf(false)
    }
    var hasBeenSelected by remember{ mutableStateOf(false) }
    var markerShow by remember { mutableStateOf(false) }

    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Toast.makeText(context, "worked", Toast.LENGTH_SHORT).show()
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
    myRef.addValueEventListener(listener)
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(10.dp))
            DriverHomeTopAppBar(Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = Shapes.medium,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Find breakdowns on map",
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp, 8.dp, 8.dp ,12.dp)
                    )
                    Divider(
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                        modifier = Modifier.padding(30.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "the breakdowns will appear, choose the one the one you want to help")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = Shapes.medium,
                modifier = Modifier.weight(1f),
                tonalElevation = 4.dp
            ) {
                GoogleMap(
                    modifier = Modifier,
                    onMapLoaded = { isLoaded = true },
                    cameraPositionState = cameraPositionState,
                    uiSettings = mapUiSettings,
                    properties = mapProperties,
                    onMapLongClick = { latLng ->
                        //cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng,15f)
                        destination.position = latLng
                        markerShow = true
                        confirmButtonEnabled = true
                    },
                    onMapClick = { latLng ->
                        markerShow = true
                        hasBeenSelected = false
                    }
                ) {

                    val markerIconSize = 90
                    val icon = bitmapDescriptorFromVector(
                        context = LocalContext.current,
                        vectorResId = R.drawable.loaction,
                        targetWidth = markerIconSize,
                        targetHeight = markerIconSize
                    )
                    Marker(
                        state = locationTowTruck,
                        draggable = true,
                        onClick = {loc->
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(locationTowTruck.position,12f)
                            return@Marker false
                        },
                        title = "current location",
                        icon = icon
                    )
                    if (markerShow){
                        viewModel.uiState.value.locationsList?.forEach{ item ->
                            val marklocation = rememberMarkerState(position = LatLng(item.currentLocationLat, item.currentLocationLong))
                            Marker(
                                state = marklocation,
                                draggable = true,
                                title = "${item.carType} ${item.carColor} ${item.carYear}",
                                onClick = {
                                    Toast.makeText(context, "jat ${item.carColor}", Toast.LENGTH_SHORT).show()

                                    currentSelectionLocation.position = LatLng(item.currentLocationLat, item.currentLocationLong)
                                    currentSelectionDestination.position = LatLng(item.destinationLocationLat, item.destinationLocationLong)
                                    documentID = item.id
                                    clientID = item.cID
                                    clientPhoneNumber = item.clientPhoneNumber
                                    token = item.token
                                    carType = item.carType
                                    carYear = item.carYear
                                    carColor = item.carColor
                                    hasBeenSelected = true
                                    markerShow = false
                                    false
                                },
                                icon = bitmapDescriptorFromVector(
                                    context = LocalContext.current,
                                    vectorResId = R.drawable.location_03,
                                    targetWidth = markerIconSize,
                                    targetHeight = markerIconSize
                                )
                            )
                        }
                    }
                    if (hasBeenSelected){
                        Marker(
                            state = currentSelectionLocation,
                            draggable = true,
                            title = "panne selected",
                            icon = bitmapDescriptorFromVector(
                                context = LocalContext.current,
                                vectorResId = R.drawable.location_03,
                                targetWidth = markerIconSize,
                                targetHeight = markerIconSize
                            )
                        )
                        Marker(
                            state = currentSelectionDestination,
                            draggable = true,
                            title = "Destination",
                            icon = bitmapDescriptorFromVector(
                                context = LocalContext.current,
                                vectorResId = R.drawable.location_04,
                                targetWidth = markerIconSize,
                                targetHeight = markerIconSize
                            )
                        )
                    }
                    /*
                    locations?.forEach {
                        val Marklocation = rememberMarkerState(position = LatLng(it.currentLocationLat, it.currentLocationLong))
                        Marker(
                            state = Marklocation,
                            draggable = true,
                            title = "${it.carType} ${it.carColor} ${it.carYear}",
                        )
                    }*/
                }
            }
            //Text(text = viewModel.location.carType.value)
            //Text(text = viewModel.location.carColor.value)
            //Text(text = viewModel.location.carYear.value)

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        db.collection("broken cars")
                            .get()
                            .addOnSuccessListener { result ->
                            for (document in result) {
                                val cID = document.get("Id").toString()
                                val clientPhoneNumber = document.get("phone number").toString()
                                val idDocs = document.id
                                val clientToken = document.get("token").toString()
                                val carTypeE = document.get("car type").toString()
                                val carYearE = document.get("car year").toString()
                                val carColorE = document.get("car color").toString()
                                val latitude = document.get("current location Latitude").toString().toDouble()
                                val longitude = document.get("current location Longitude").toString().toDouble()
                                val destinationLat =
                                    document.getDouble("destination location Latitude").toString().toDouble()
                                val destinationLong =
                                    document.getDouble("destination location Longitude").toString().toDouble()
                                locations.add(
                                    BrokenCar(
                                        idDocs,
                                        cID,
                                        clientToken,
                                        carTypeE,
                                        carYearE,
                                        carColorE,
                                        latitude,
                                        longitude,
                                        destinationLat,
                                        destinationLong,
                                        clientPhoneNumber
                                    )
                                )
                                viewModel.updateMarkersList(locations)
                            }

                        }
                            .addOnFailureListener { exeption->
                                Toast.makeText(context, "jat $exeption", Toast.LENGTH_SHORT).show()
                            }
                        markerShow = !markerShow
                              },
                    modifier
                        .padding(6.dp, 6.dp, 6.dp, 0.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(text = "brokens",
                        fontSize = 14.sp,
                        style = Typography.displayLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        val centerPoint = calculateCenter(currentSelectionDestination.position, currentSelectionLocation.position)
                        val zoomLevel = 10.0f
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            centerPoint,
                            zoomLevel
                        )
                    },
                    modifier
                        .padding(6.dp, 6.dp, 6.dp, 0.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    enabled = hasBeenSelected
                ) {
                    Text(
                        text = "Destination",
                        fontSize = 14.sp,
                        style = Typography.displayLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.White,

                    )
                }

                Button(
                    onClick = {

                        /*db.collection("broken cars").document(documentID)
                            .delete()
                            .addOnSuccessListener { Toast.makeText(context, "document deleted", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(context, "document didn't deleted", Toast.LENGTH_SHORT).show() }
*/
                        val requestAnswered = hashMapOf(
                            "tow truck" to auth.currentUser?.uid,
                            "driver" to clientID,
                            "driver phone number" to clientPhoneNumber,
                            "car type" to carType,
                            "car year" to carYear,
                            "car color" to carColor,
                            "date" to LocalDate.now().toString(),
                            "time" to LocalTime.now().toString(),
                            "tow truck current location Latitude" to locationTowTruck.position.latitude,
                            "tow truck current location Longitude" to locationTowTruck.position.latitude,
                            "client location Longitude" to currentSelectionLocation.position.latitude,
                            "client location Longitude" to currentSelectionLocation.position.longitude,
                            "destination location Latitude" to currentSelectionDestination.position.latitude,
                            "destination location Longitude" to currentSelectionDestination.position.longitude,
                        )
                        db.collection("onProcessingRequests").document(auth.currentUser?.uid.toString())
                            .set(requestAnswered).addOnCompleteListener { task->
                                if (task.isSuccessful){
                                    myRef.setValue("calling")
                                    viewModel.updateClientID(clientID)
                                    viewModel.updateInRequest(true)
                                    confirmRequest()
                                }else{
                                    Toast.makeText(context, "request add failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier
                        .padding(6.dp, 6.dp, 6.dp, 0.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    enabled = hasBeenSelected
                ) {
                    Text(
                        text = "confirm",
                        fontSize = 14.sp,
                        style = Typography.displayLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.scrim
                    )
                }
            }

        }
        if (!isLoaded) {
            CircularProgressIndicator()
        }
    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, targetWidth: Int, targetHeight: Int): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    val originalWidth = drawable.intrinsicWidth
    val originalHeight = drawable.intrinsicHeight
    val scaleFactor = Math.min(
        targetWidth.toFloat() / originalWidth.toFloat(),
        targetHeight.toFloat() / originalHeight.toFloat()
    )
    val resizedWidth = (originalWidth * scaleFactor).toInt()
    val resizedHeight = (originalHeight * scaleFactor).toInt()
    val bitmap = Bitmap.createBitmap(resizedWidth, resizedHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, resizedWidth, resizedHeight)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
fun calculateCenter(location1: LatLng, location2: LatLng): LatLng {
    val lat = (location1.latitude + location2.latitude) / 2
    val lng = (location1.longitude + location2.longitude) / 2
    return LatLng(lat, lng)
}