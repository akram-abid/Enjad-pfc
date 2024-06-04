package com.example.depannage.screens.clientHomeScreen

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.depannage.R
import com.example.woof.ui.theme.Shapes
import com.example.woof.ui.theme.Typography
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Composable
fun DriverHomeScreen(
    viewModel: DriverHomeViewModel,
    onAddLoc: ()-> Unit,
    onProfileSurfaceClick: ()-> Unit,
    toPostPhoneNumber:()-> Unit ,
    modifier: Modifier
    ){
    Scaffold(
        topBar = {
            //DriverHomeTopAppBar(Modifier.fillMaxWidth(), Back = Back)
        },
        modifier = Modifier,
        ) {
        DriverHomeElements(
            data = GlobalData(),
            onAddPos = onAddLoc,
            viewModel = viewModel,
            onProfileSurfaceClick = onProfileSurfaceClick,
            toPostPhoneNumber = toPostPhoneNumber,
            modifier = Modifier.padding(it)
        )
    }

}

@Composable
fun DriverHomeElements(
    data : GlobalData,
    onAddPos: () -> Unit,
    toPostPhoneNumber:()-> Unit ,
    viewModel: DriverHomeViewModel,
    onProfileSurfaceClick: ()-> Unit,
    modifier: Modifier,
) {

    val auth = FirebaseAuth.getInstance()
    var towID by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var carType by remember { mutableStateOf("") }
    var carYear by remember { mutableStateOf("") }
    var carColor by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = Firebase.firestore
    val database = Firebase.database
    val myRef = database.getReference(auth.currentUser?.uid!!)
    var status by remember { mutableStateOf("can") }

    var userID by remember{ mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var username by remember{ mutableStateOf(auth.currentUser?.uid.toString()) }
    var phoneNumber by remember{ mutableStateOf("phone number") }
    var province by remember{ mutableStateOf("province") }
    var city by remember{ mutableStateOf("city") }
    var profilePic by remember{ mutableStateOf("profile pic") }
    var password by remember{ mutableStateOf("") }


    db.collection("users")
        .document(auth.currentUser?.uid.toString())
        .get()
        .addOnSuccessListener { result ->
            userID = result.get("id").toString()
            username = result.get("username").toString()
            //password = document.get("password").toString()
            phoneNumber = result.get("phone number").toString()
            city = result.get("city").toString()
            province = result.get("province").toString()
            email = result.get("email").toString()
            profilePic = result.get("profile pic").toString()
            password = result.get("password").toString()

            viewModel.updateProfileInfos(
                username,
                email,
                phoneNumber,
                province,
                city,
                profilePic,
                password
            )
        }

    var locationText by remember { mutableStateOf("") }
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK)
            Log.d("appDebug", "Accepted")
        else {
            Log.d("appDebug", "Denied")
        }
    }

    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            status = snapshot.value.toString()
        }

        override fun onCancelled(error: DatabaseError) {

        }

    }
    myRef.addValueEventListener(listener)
    var requst2ndOrder by remember { mutableStateOf(false) }

    var PicState by remember { mutableStateOf(false) }
    PicState = if (profilePic == "") false else true
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        val defaultCameraPositionState = CameraPosition.fromLatLngZoom(viewModel.Location.location.value, 12f)
        val cameraPositionState = rememberCameraPositionState{
            position = defaultCameraPositionState

        }
        var mapShow by remember { mutableStateOf(false) }
        val location = rememberMarkerState(position = viewModel.Location.destination.value)
        val destination = viewModel.uiState.value.destination?.let { rememberMarkerState(position = it) }
            if(mapShow){
                AlertDialog(
                    onDismissRequest = { /*showDialog = false*/ },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                      mapShow = false
                            },
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                        ) {
                            Text(
                                text = "Post",
                                fontSize = 18.sp
                            )
                        }
                    },
                    dismissButton = {

                    },
                    title = {
                        Text(
                            text = "Current request",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    },
                    text = {
                        Column {
                            GoogleMap (
                                modifier = Modifier.height(330.dp),
                                cameraPositionState = cameraPositionState
                            ){
                                Marker(
                                    state = location
                                )
                            }
                        }
                    })
            }
            if (requst2ndOrder) {
                AlertDialog(
                    onDismissRequest = { /*showDialog = false*/ },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onAddPos()
                                db.collection("broken cars")
                                    .document(auth.currentUser?.uid.toString())
                                    .delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "the previous post  has benn deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                viewModel.updateUserCurrentLocation(
                                    context,
                                    settingResultRequest
                                ) { location ->
                                    locationText = location
                                    showDialog =
                                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                                                ActivityCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                                ) == PackageManager.PERMISSION_GRANTED &&
                                                ActivityCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                ) == PackageManager.PERMISSION_GRANTED
                                }
                                viewModel.onServiceTypeChanged("car")
                                requst2ndOrder = false

                            },
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                        ) {
                            Text(
                                text = "Post",
                                fontSize = 18.sp
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                requst2ndOrder = false
                            },
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 18.sp
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "you already posted a request",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    },
                    text = {
                        Column {

                            Text(text = "you have a request posted with your name and state , do you want to post another,")
                            Text(text = "this will delete the first request")
                        }
                    })
            }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { /*showDialog = false*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAddPos()
                            //view model function
                            showDialog = false

                        },
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 18.sp
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                        },
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 18.sp
                        )
                    }
                },
                title = {
                    Text(
                        text = "Fill the informations to complete the request",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Car Characteristics",
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = carType,
                            onValueChange = {
                                carType = it
                                viewModel.onCarTypeChange(it)
                                //data.updateType(it)
                            },
                            label = { Text("type") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsCar,
                                    contentDescription = "type"
                                )
                            },
                            shape = Shapes.medium,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your car type") },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = carYear,
                            onValueChange = {
                                carYear = it
                                viewModel.onCarYearChange(it)
                                //data.updateYear(it)
                            },
                            label = { Text("year") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "year"
                                )
                            },
                            shape = Shapes.medium,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your car year") },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = carColor,
                            onValueChange = {
                                carColor = it
                                viewModel.onCarColorChange(it)
                                //data.updateColor(it)
                            },
                            label = { Text("color") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Colorize,
                                    contentDescription = "color"
                                )
                            },
                            shape = Shapes.medium,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your car color") },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                    }
                },
                shape = Shapes.medium,
                tonalElevation = 4.dp,
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier
                .padding(12.dp, 3.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                fontSize = 20.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "profile"
            )
        }
        /*
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSecondary,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 4.dp
        ) {*/
        Button(
            onClick = {

                      },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 12.dp, 12.dp, 0.dp)
            ) {
                //Image(painter = , contentDescription = "circles background")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!PicState) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "profile picture",
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(80.dp)
                                    .padding(0.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = profilePic,
                                contentDescription = "Carte Grise",
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(80.dp)
                                    .padding(0.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.profile),
                                placeholder = painterResource(R.drawable.profile)
                            )
                        }
                        Text(
                            text = username,
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.scrim,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(12.dp, 0.dp)
                                .weight(4f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { viewModel.isClientUpdate(true)
                            onProfileSurfaceClick()}) {
                            Text(
                                text = "edit profile",
                                style = Typography.displayLarge,
                                fontSize = 16.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                contentDescription = "edit profile"
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        if (status == "waiting") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.inversePrimary.copy(0.3f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Text(
                            text = "Waiting for depannage",
                            fontSize = 20.sp,
                            style = Typography.displayLarge
                        )
                        Text(
                            text = "we have posted your help request, please wait for someone to call you.",
                            fontSize = 14.sp,
                            style = Typography.displayMedium
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.request_stauts_02_02),
                        contentDescription = "status request image",
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp, 0.dp, 0.dp, 16.dp)
                    )
                }
            }
        } else if (status == "calling") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green.copy(0.3f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Text(
                            text = "Help is coming",
                            fontSize = 20.sp,
                            style = Typography.displayLarge
                        )
                        Text(
                            text = "a deppange saw you help request, he is going to call you now",
                            fontSize = 14.sp,
                            style = Typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row (
                        verticalAlignment = Alignment.CenterVertically
                        ){
                            Button(
                                onClick = { myRef.setValue("accepted")
                                          db.collection("broken cars").document(auth.currentUser?.uid.toString()).delete()
                                          },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
                            ) {
                                Text(
                                    text = "accept",
                                    color = Color.White,
                                    style = Typography.displayLarge
                                )
                            }
                            Button(
                                onClick = { myRef.setValue("waiting")
                                    db.collection("onProcessingRequests")
                                        .get().addOnSuccessListener {  doucuments->
                                            for (document in doucuments){
                                                if (document.get("driver") == auth.currentUser?.uid.toString()){
                                                    towID = document.get("tow truck").toString()
                                                    db.collection("onProcessingRequests").document(towID).delete().addOnCompleteListener {
                                                        task-> if (task.isSuccessful){
                                                            Toast.makeText(context, "frat", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
                            ) {
                                Text(
                                    text = "refuse",
                                    color = Color.White,
                                    style = Typography.displayLarge
                                )
                            }
                        }

                    }
                    Image(
                        painter = painterResource(id = R.drawable.request_stauts_01),
                        contentDescription = "status request image",
                        modifier = Modifier
                            .height(155.dp)
                            .weight(1f)
                            .padding(0.dp, 0.dp, 0.dp, 16.dp)
                    )
                }
            }
        }else if (status== "accepted"){
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green.copy(0.3f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Text(
                            text = "Confirm order state",
                            fontSize = 20.sp,
                            style = Typography.displayLarge
                        )
                        Text(
                            text = "click to confirm that the tow truck has did his job suceccfully",
                            fontSize = 14.sp,
                            style = Typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Button(
                                onClick = {
                                    myRef.removeValue()
                                    db.collection("onProcessingRequests")
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                if (document.get("driver") == auth.currentUser?.uid.toString()) {
                                                    towID = document.get("tow truck").toString()
                                                    break
                                                }
                                            }
                                            if (towID.isNotEmpty()) {
                                                db.collection("onProcessingRequests").document(towID).delete()
                                                    .addOnSuccessListener {
                                                        // handle success
                                                    }
                                                    .addOnFailureListener { e ->
                                                        // handle failure
                                                    }
                                            } else {
                                                // handle case where towID is not found
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            // handle failure to get documents
                                        }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp)
                            ) {
                                Text(
                                    text = "confirm order",
                                    color = Color.White,
                                    style = Typography.displayLarge
                                )
                            }
                        }

                    }
                    /*
                    Image(
                        painter = painterResource(id = R.drawable.request_stauts_01),
                        contentDescription = "status request image",
                        modifier = Modifier
                            .height(155.dp)
                            .weight(1f)
                            .padding(0.dp, 0.dp, 0.dp, 16.dp)
                    )*/
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(12.dp, 4.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logopng_08),
                        contentDescription = "logo",
                        modifier = Modifier
                            .width(165.dp)
                            .padding(16.dp)
                    )
                    Text(
                        text = "Enjad for roadside assistance",
                        fontSize = 32.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp, 0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Services",
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Services"
                )
            }
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if(status== "waiting"){
                            requst2ndOrder = true
                        }else{
                            viewModel.updateUserCurrentLocation(
                                context,
                                settingResultRequest
                            ) { location ->
                                locationText = location
                                showDialog =
                                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                                            ActivityCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            ) == PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            ) == PackageManager.PERMISSION_GRANTED
                                viewModel.onServiceTypeChanged("car")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 0.dp, 4.dp, 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp, 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.project_01),
                            contentDescription = "map solution"
                        )
                        Spacer(modifier = Modifier.padding(14.dp))
                        Text(
                            text = "post location on map",
                            style = Typography.bodyLarge,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 8.dp),
                            color = MaterialTheme.colorScheme.scrim
                        )
                    }
                }
                Button(
                    onClick = {toPostPhoneNumber() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp, 0.dp, 0.dp, 0.dp)
                )
                {
                    Column(
                        modifier = Modifier.padding(6.dp, 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.project_02),
                            contentDescription = "phone number solution"
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            text = "get phone number",
                            style = Typography.bodyLarge,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 8.dp),
                            color = MaterialTheme.colorScheme.scrim
                        )
                    }
                }
            }
            Button(
                onClick = {
                    if(status== "waiting"){
                        requst2ndOrder = true
                    }else{
                        viewModel.updateUserCurrentLocation(
                            context,
                            settingResultRequest
                        ) { location ->
                            locationText = location
                            showDialog =
                                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                                        ActivityCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED
                            viewModel.onServiceTypeChanged("excavator")
                        }
                    }
                },
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .height(140.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text(
                            text = "excavators transportation",
                            style = Typography.displayLarge,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.scrim
                        )
                        Text(
                            text = "find work for excavator transport",
                            fontSize = 14.sp,
                            style = Typography.displaySmall,
                            color = MaterialTheme.colorScheme.scrim
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(12.dp, 0.dp, 0.dp, 0.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.excvator_01),
                            contentDescription = "Port de char",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(0.dp, 0.dp, 6.dp, 0.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun DriverHomeTopAppBar(
modifier: Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
            ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back",
            modifier = Modifier
                .padding(8.dp, 0.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        /*Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "back",
            modifier = Modifier
                .padding(8.dp, 0.dp)
        )*/
    /*Image(
        painter = painterResource(id = R.drawable.untitled_1__recovered__02),
        contentDescription = "profile pic",
        modifier = Modifier
            .aspectRatio(1.0f)
            .padding(vertical = 20.dp, horizontal = 0.dp),
    )*/
    }

}

@Singleton
class GlobalData @Inject constructor(){
    private val _location = MutableStateFlow(LatLng(0.0, 0.0))
    val location = _location.asStateFlow()
    private val _carType = MutableStateFlow("")
    val carType = _carType.asStateFlow()
    private val _carColor = MutableStateFlow("")
    val carColor = _carColor.asStateFlow()
    private val _carYear = MutableStateFlow("")
    val carYear = _carYear.asStateFlow()
    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()
    private val _Destiantion = MutableStateFlow(LatLng(0.0, 0.0))
    val destination = _Destiantion.asStateFlow()

    fun updatedestiantion(newLocation: LatLng){
        _Destiantion.value = newLocation
    }
    fun update(newLocation: LatLng){
        _location.value = newLocation
    }
    fun updateType(newLocation: String){
        _carType.value = newLocation
    }
    fun updateToken(newLocation: String){
        _token.value = newLocation
    }
    fun updateColor(newLocation: String){
        _carColor.value = newLocation
    }
    fun updateYear(newLocation: String){
        _carYear.value = newLocation
    }
}

@Singleton
class Profileinfos @Inject constructor(){
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()
    private val _province = MutableStateFlow("")
    val province = _province.asStateFlow()
    private val _city = MutableStateFlow("")
    val city = _city.asStateFlow()
    private val _profilePic = MutableStateFlow("")
    val profilePic = _profilePic.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _isClient = MutableStateFlow(true)
    val isClient = _isClient.asStateFlow()

    fun updateUsername(newLocation: String){
        _username.value = newLocation
    }
    fun updateEmail(newLocation: String){
        _email.value = newLocation
    }
    fun updatePhoneNumber(newLocation: String){
        _phoneNumber.value = newLocation
    }
    fun updateProvinvce(newLocation: String){
        _province.value = newLocation
    }
    fun updatecity(newLocation: String){
        _city.value = newLocation
    }
    fun updateProfilePic(newLocation: String){
        _profilePic.value = newLocation
    }
    fun updatePassword(newLocation: String){
        _password.value = newLocation
    }
    fun updateIsClient(newLocation: Boolean){
        _isClient.value = newLocation
    }
}
@Singleton
class ServiceType @Inject constructor(){
    private val _serviceType = MutableStateFlow("")
    val serviceType = _serviceType.asStateFlow()

    fun updateService(newValue: String){
        _serviceType.value = newValue
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewDriverHomeElements(){
    DepannageTheme {
        DriverHomeElements(onAddPos = { }, modifier = Modifier, viewModel = hiltViewModel(), data = GlobalData())
    }
}*/