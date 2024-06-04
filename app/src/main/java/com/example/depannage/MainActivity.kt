package com.example.depannage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.depannage.ui.theme.DepannageTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

val algeriaState = LatLng(28.00, 3.00)

val defaultCameraPositionState = CameraPosition.fromLatLngZoom(algeriaState, 4f)


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var auth: FirebaseAuth

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("cc", "granted coarse")
            }

            permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d("cc", "granted Fine")
            }

            else -> {
                Log.d("cc", "denied")
            }
        }
    }

    private fun requestPermissions() {

        val finePermission: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarsePermission: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest: MutableList<String> = ArrayList()

        if (!finePermission) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!coarsePermission) {
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (permissionRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }


    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            DepannageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DepannageApp(modifier = Modifier.fillMaxSize(),
                        { requestPermissions() })
                       //{requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))})
                }
            }
        }

        //requestLocationPermission()
        requestPermissions()

    }
}



