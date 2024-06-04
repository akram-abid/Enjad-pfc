package com.example.depannage.screens.login


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,

): ViewModel() {
    val db = Firebase.firestore
    val uiState = mutableStateOf(LoginUiState())

    var isCircularIndicatorVisible = mutableStateOf(false)

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }
    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSigninClick(toDriverHomeScreen: ()-> Unit, toDepHomeScreen: ()-> Unit, toRefuse:()-> Unit, toProcessing:()->Unit){
        //the variable that lets the circular indicator appears when this function is called until
        //the sign in process is finished whether success or fail
        isCircularIndicatorVisible.value = true
        //sign in process started
        auth.signInWithEmailAndPassword(uiState.value.email, uiState.value.password)
            .addOnCompleteListener{
                //the process completed successfully
                if (it.isSuccessful){
                    isCircularIndicatorVisible.value = false
                    db.collection("users").document(auth.currentUser?.uid.toString())
                        .get().addOnSuccessListener {
                            if (it.exists()){
                                toDriverHomeScreen()
                            }else{
                                db.collection("workers").document(auth.currentUser?.uid.toString())
                                    .get().addOnSuccessListener { document->
                                        if (document.get("account state") == "unactivated"){
                                            toProcessing
                                        } else if(document.get("account state") == "activated"){
                                            toDepHomeScreen()
                                        }else{
                                            toRefuse()
                                        }
                                    }
                            }
                        }
                    Log.d("MainActivity","sucess done great")
                }
            }
            .addOnFailureListener {
                //the process failed
                Log.d("MainActivity","we failed captain please forgive us")
            }
    }
}