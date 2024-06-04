package com.example.depannage.screens.depannageHomeScreen

import android.Manifest
import android.app.Activity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.depannage.R
import com.example.depannage.screens.clientHomeScreen.DriverHomeTopAppBar
import com.example.woof.ui.theme.Typography
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Composable
fun DepannageHomeScreen(
    onEditProfileClicked: ()-> Unit,
    navigateToMap: () -> Unit,
    toPostPhoneNumber :()->Unit,
){
   DepannageHomeScreenElements(
       viewModel = hiltViewModel(),
       navigateToMap = navigateToMap,
       onEditProfileClicked = onEditProfileClicked,
       toPostPhoneNumber = toPostPhoneNumber,
   )
}

@Composable
fun DepannageHomeScreenElements(
    onEditProfileClicked: ()-> Unit,
    viewModel: DepannageHomeScreenViewModel,
    toPostPhoneNumber :()->Unit,
    navigateToMap: ()-> Unit
){
    val context = LocalContext.current
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK)
            Log.d("appDebug", "Accepted")
        else {
            Log.d("appDebug", "Denied")
        }
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    var userID by remember{ mutableStateOf("") }
    var clientID by remember{ mutableStateOf("") }
    var clientPhoneNumber by remember{ mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var username by remember{ mutableStateOf(auth.currentUser?.uid.toString()) }
    var phoneNumber by remember{ mutableStateOf("phone number") }
    var province by remember{ mutableStateOf("province") }
    var city by remember{ mutableStateOf("city") }
    var profilePic by remember{ mutableStateOf("profile pic") }
    var password by remember{ mutableStateOf("") }
    val database = Firebase.database
    val myRef = database.getReference(clientID)
    var PicState by remember { mutableStateOf(false) }
    PicState = if (profilePic == "") false else true

    var status by remember { mutableStateOf("can") }
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            status = snapshot.value.toString()
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    myRef.addValueEventListener(listener)
    db.collection("workers")
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
    db.collection("onProcessingRequests").document(auth.currentUser?.uid.toString())
        .get().addOnSuccessListener { task->
            clientID = task.get("driver").toString()
            clientPhoneNumber = task.get("driver phone number").toString()
        }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        DriverHomeTopAppBar(modifier = Modifier.fillMaxWidth())
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 12.dp, 12.dp, 0.dp)
                ) {
                    /*Image(
                    painter = painterResource(id = R.drawable.untitled_1_01),
                    contentDescription = "circles background",
                    alpha = 0.6f,
                    modifier = Modifier.fillMaxWidth()
                )*/
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(6.dp)
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
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .weight(4f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {
                                viewModel.isClientUpdate(false)
                                onEditProfileClicked()
                            }) {
                                Text(
                                    text = "edit profile",
                                    style = Typography.displayLarge,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(16.dp))
        if(status == "calling"){
            db.collection("onProcessingRequests").document(auth.currentUser?.uid.toString())
                .get().addOnSuccessListener { task->
                    clientID = task.get("driver").toString()
                    clientPhoneNumber = task.get("driver phone number").toString()
                }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.inversePrimary.copy(0.3f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
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
                            text = "Waiting for Driver to respond",
                            fontSize = 20.sp,
                            style = Typography.displayLarge
                        )
                        Text(
                            text = "we have send your help offer,call him with   "+clientPhoneNumber,
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
        } else {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(12.dp, 0.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
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
        Spacer(modifier = Modifier.height(20.dp))
        Divider(thickness = 3.dp, modifier = Modifier.padding(20.dp, 0.dp))
        Spacer(modifier = Modifier.height(11.dp))

        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp, 6.dp)
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
            Text(
                text = "choose how you want to work",
                fontSize = 16.sp,
                style = Typography.displayMedium,
                modifier = Modifier.padding(12.dp, 2.dp, 0.dp, 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.updateUserCurrentLocation(context, settingResultRequest){location ->
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                                ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(context, "ok this your location", Toast.LENGTH_SHORT).show()
                                db.collection("onProcessingRequests").document(auth.currentUser?.uid.toString())
                                    .get().addOnSuccessListener { document->
                                        if(!document.exists()){
                                            navigateToMap()
                                        }
                                    }
                            }else{
                                Toast.makeText(context, "please enable the the location", Toast.LENGTH_SHORT).show()
                            }
                        }
                        viewModel.updateCarRequestStatus(true)
                        viewModel.updateExcavatorRequestStatus(false)
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
                            text = "breakdowns from map",
                            style = Typography.bodyLarge,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 8.dp),
                            color = MaterialTheme.colorScheme.scrim
                        )
                    }
                }
                Button(
                    onClick = {
                              toPostPhoneNumber()
                    },
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
                            text = "post phone number",
                            style = Typography.bodyLarge,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 8.dp),
                            color = MaterialTheme.colorScheme.scrim
                        )
                    }
                }
            }
            Text(
                text = "huge vehicle transport",
                fontSize = 16.sp,
                style = Typography.displayMedium,
                modifier = Modifier.padding(12.dp,6.dp, 0.dp, 0.dp)
            )
            Button(
                onClick = {
                    viewModel.updateUserCurrentLocation(context, settingResultRequest){location ->
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                            ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(context, "ok this your location", Toast.LENGTH_SHORT).show()
                            navigateToMap()
                        }else{
                            Toast.makeText(context, "please enable the the location", Toast.LENGTH_SHORT).show()
                        }
                    }

                    viewModel.updateCarRequestStatus(false)
                    viewModel.updateExcavatorRequestStatus(true)
                },
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .height(140.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = Color.White),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
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

                    Row (
                        modifier = Modifier
                            .padding(12.dp, 0.dp, 0.dp, 0.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
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
        Button(onClick = {
            viewModel.logOut()
        }) {
            Text(text = viewModel.uiState.value.currentLocation.toString())
            Text(text = "log out")
        }
    }
}

@Singleton
class RequestType @Inject constructor() {
    private val _location = MutableStateFlow(LatLng(0.0, 0.0))
    val location = _location.asStateFlow()
    private val _carRequest = MutableStateFlow(false)
    val carType = _carRequest.asStateFlow()
    private val _excavatorRequest = MutableStateFlow(false)
    val carYear = _excavatorRequest.asStateFlow()
    private val _inRequest = MutableStateFlow(false)
    val inRequest = _inRequest.asStateFlow()
    private val _clientID = MutableStateFlow("")
    val clientID= _clientID.asStateFlow()

    fun updateLocation(newLocation: LatLng){
        _location.value = newLocation
    }

    fun updateCarRequest(newValue: Boolean) {
        _carRequest.value = newValue
    }

    fun updateExcavator(newValue: Boolean) {
        _excavatorRequest.value = newValue
    }
    fun updateInRequest(newValue: Boolean) {
        _inRequest.value = newValue
    }
    fun updateClientID(newValue: String) {
        _clientID.value = newValue
    }
}
