package com.example.depannage.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun WaitScreen(
){
    Surface (
        color = MaterialTheme.colorScheme.background
    ){
        val auth = FirebaseAuth.getInstance()

    }
}