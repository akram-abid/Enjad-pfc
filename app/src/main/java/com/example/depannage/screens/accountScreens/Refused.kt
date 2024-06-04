package com.example.depannage.screens.accountScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun Refused(
    toLogin:()-> Unit
){
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(12.dp)
    ){
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = "error",
            tint = Color.Red,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "we have refused you account creation request due to incompatible proof you've send, please retry again and send an apropiate proof")
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            db.collection("workers").document(auth.currentUser?.uid.toString())
                .delete()
            auth.signOut()
            toLogin()
        }) {
            Text(text = "retry")
        }

    }
}