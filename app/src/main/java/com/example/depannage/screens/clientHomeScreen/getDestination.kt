package com.example.depannage.screens.clientHomeScreen

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.depannage.R
import com.example.woof.ui.theme.Shapes
import com.example.woof.ui.theme.Typography
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDate


val DEFAULT_POS = LatLng(0.0, 0.0)

@Composable
fun GetDestinationScreen(
    cancelRequest: ()-> Unit,
    confirmRequest:()-> Unit,
    viewModel: DriverHomeViewModel
){

    Scaffold(
        modifier = Modifier,
    ) {
        GetDestination(
            cancelRequest = cancelRequest,
            confirmRequest = confirmRequest,
            modifier = Modifier.padding(it),
            viewModel = viewModel
        )
    }
}


@Composable
fun GetDestination(
    cancelRequest: ()-> Unit,
    confirmRequest: ()-> Unit,
    viewModel: DriverHomeViewModel ,
    modifier : Modifier,
){
    val db = Firebase.firestore
    val defaultCameraPositionState = CameraPosition.fromLatLngZoom(viewModel.Location.location.value, 12f)
    var hasLocationPermission by remember {
        mutableStateOf(false)
    }
    val locationState = rememberMarkerState(
        position = viewModel.Location.location.value
    )
    val database = Firebase.database
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val userUID = auth.currentUser?.uid
    val myRef = database.getReference(userUID!!)

    var destination = rememberMarkerState(position = DEFAULT_POS)

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
    var token by remember {
        mutableStateOf("vide")
    }
    var markerShow by remember { mutableStateOf(false) }
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
                        text = "Find Route",
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
                    Text(text = viewModel.Location.token.value)
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
                        viewModel.updateDestination(latLng)
                        markerShow = true
                        confirmButtonEnabled = true
                    }
                ) {
                    val location = rememberMarkerState(position = viewModel.Location.location.value)
                    val markerIconSize = 90
                    val icon = bitmapDescriptorFromVector(
                        context = LocalContext.current,
                        vectorResId = R.drawable.loaction,
                        targetWidth = markerIconSize,
                        targetHeight = markerIconSize
                    )
                    Marker(
                        state = location,
                        draggable = true,
                        onClick = {loc->
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(location.position,30f)
                            return@Marker false
                        },
                        title = "current location",
                        icon = icon
                    )
                    if (markerShow) {
                        Marker(
                            state = destination,
                            draggable = true,
                            onClick = {
                                return@Marker false
                            },
                            title = "Algeria Map Title",
                            icon = bitmapDescriptorFromVector(
                                    context = LocalContext.current,
                                    vectorResId = R.drawable.location_04,
                                    targetWidth = markerIconSize,
                                    targetHeight = markerIconSize
                                )
                        )
                    }

                }
            }
            //Text(text = viewModel.location.carType.value)
            //Text(text = viewModel.location.carColor.value)
            //Text(text = viewModel.location.carYear.value)

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = { cancelRequest() },
                    modifier
                        .padding(12.dp, 6.dp, 12.dp, 0.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(text = "cancel",
                        fontSize = 16.sp,
                        style = Typography.displayLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                        )
                }

                Button(
                    onClick = {
                        val fcm = Firebase.messaging.token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                token = task.result.toString()
                            } else {

                            }
                        }
                        //confirmRequest()
                        val request = hashMapOf(
                            "username" to auth.currentUser?.email.toString(),
                            "phone number" to viewModel.profileInfos.phoneNumber.value,
                            "Id" to auth.currentUser?.uid.toString(),
                            "car type" to viewModel.Location.carType.value,
                            "car year" to viewModel.Location.carYear,
                            "car color" to viewModel.Location.carColor.value,
                            "token" to viewModel.Location.token.value,
                            "date" to LocalDate.now().toString(),
                            "current location Latitude" to viewModel.Location.location.value.latitude,
                            "current location Longitude" to viewModel.Location.location.value.longitude,
                            "destination location Latitude" to viewModel.uiState.value.destination?.latitude,
                            "destination location Longitude" to viewModel.uiState.value.destination?.longitude,
                        )
                        if (viewModel.serviceType.serviceType.value == "car") {
                            db.collection("broken cars").document(auth.currentUser?.uid.toString())
                                .set(request).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        myRef.setValue("waiting").addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                        } else {

                                        }
                                        }
                                        confirmRequest()
                                    }
                                }
                                    } else if (viewModel.serviceType.serviceType.value == "excavator") {
                                        db.collection("broken excavators").document(auth.currentUser?.uid.toString())
                                            .set(request).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    myRef.setValue("waiting")
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "real time",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    "no real time",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    confirmRequest()
                                                }
                                            }
                                    }
                                }
                            ,
                    modifier
                        .padding(12.dp, 6.dp, 12.dp, 0.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    enabled = confirmButtonEnabled
                    ) {
                    Text(
                        text = "confirm",
                        fontSize = 16.sp,
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


/*@Preview(showBackground = true)
@Composable
fun getDestinationPreview(){
    DepannageTheme {
        GetDestination(modifier = Modifier.fillMaxSize())
    }
}*/
