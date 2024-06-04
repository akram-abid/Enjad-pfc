package com.example.depannage.screens.accountScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun VerfyAccount(
    onClientVerfied:()-> Unit,
    toUnActivated:()-> Unit,
    toDepHomeScreen:()-> Unit,
    toRefused:()-> Unit
){
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    val currentUser = auth.currentUser
    var show by remember { mutableStateOf(false) }
    var show1 by remember { mutableStateOf(true) }
    if (!show) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

        }
        LaunchedEffect(currentUser) {
            currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (currentUser.isEmailVerified) {
                        val docRef = db.collection("users").document(currentUser.uid)
                        docRef.get().addOnSuccessListener { document ->
                            if (document.exists()) {
                                onClientVerfied()
                            } else {
                                db.collection("workers").document(auth.currentUser?.uid.toString())
                                    .get().addOnSuccessListener { document ->
                                        if (
                                            document.get("account state") == "unactivated"
                                        ) {
                                            toUnActivated()
                                        } else if (document.get("account state") == "activated") {
                                            toDepHomeScreen()
                                        } else if (document.get("account state") == "refused") {
                                            toRefused()
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            show = true
        }
    }else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.VerifiedUser,
                contentDescription = "ee",
                tint = Color.Yellow,
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "you have to verify your account , we've send a verfication email to you")
        }
    }
}