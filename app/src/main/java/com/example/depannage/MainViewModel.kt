package com.example.depannage

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class MainViewModel @Inject constructor(): ViewModel() {
    @SuppressLint
    fun getUserLocationAct(
        fusedLocationProviderClient: FusedLocationProviderClient
    ): Location?{
        var loc :Location? = null
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task->
                if (task.isSuccessful){
                    loc = task.result
                }
            }
        }catch (e: SecurityException){

        }
        return loc
    }
}