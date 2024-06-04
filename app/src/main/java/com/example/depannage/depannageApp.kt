package com.example.depannage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.depannage.PostPhoneNumber.PostPhoneNumber
import com.example.depannage.PostPhoneNumber.RetrivePhoneNumber
import com.example.depannage.screens.WaitScreen
import com.example.depannage.screens.accountScreens.Refused
import com.example.depannage.screens.accountScreens.UnActivated
import com.example.depannage.screens.accountScreens.VerfyAccount
import com.example.depannage.screens.accountType.AccountType
import com.example.depannage.screens.carteGrisse.CarteGrisse
import com.example.depannage.screens.clientHomeScreen.DriverHomeScreen
import com.example.depannage.screens.clientHomeScreen.GetDestinationScreen
import com.example.depannage.screens.depannageHomeScreen.DepannageHomeScreen
import com.example.depannage.screens.depannageHomeScreen.FindWorkScreen
import com.example.depannage.screens.login.LoginScreen
import com.example.depannage.screens.profile.Profile
import com.example.depannage.screens.signup.SignupScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class DepannageScreens {
    Login,
    Signup,
    HomeScreen,
    GetDestination,
    ChooseType,
    CarteGrisse,
    DepannageHomeScreen,
    FindWork,
    Profile,
    WaitScreen,
    UnActivated,
    Refused,
    PostPhoneNumber,
    RetrivePhoneNumber,
    VerifyAccount
}

@Composable
fun DepannageApp(
    modifier: Modifier = Modifier,
    requestPermission: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    var startDestination by remember { mutableStateOf(DepannageScreens.WaitScreen.name) }

    LaunchedEffect(auth.currentUser) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startDestination = DepannageScreens.Login.name
        } else {
            val docRef = db.collection("users").document(currentUser.uid)
            docRef.get().addOnSuccessListener { document ->
                 if (document.exists()) {
                     startDestination = DepannageScreens.HomeScreen.name
                } else {
                    db.collection("workers").document(auth.currentUser?.uid.toString())
                        .get().addOnSuccessListener { document->
                            if (
                                document.get("account state") == "unactivated"
                            ){
                                startDestination = DepannageScreens.UnActivated.name
                            }else if (document.get("account state") == "activated"){
                                startDestination = DepannageScreens.DepannageHomeScreen.name
                            }else if (document.get("account state") == "refused"){
                                startDestination = DepannageScreens.Refused.name
                            }
                        }
                }
            }.addOnFailureListener {
                startDestination = DepannageScreens.Login.name // Handle failure case
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(8.dp)
    ) {
        composable(DepannageScreens.Login.name) {
            LoginScreen(
                toDriverHomeScreen = {
                    navController.navigate(DepannageScreens.VerifyAccount.name) {
                        popUpTo(0) { inclusive = false }
                    }
                },
                onCreateAccount = { navController.navigate(DepannageScreens.Signup.name) },
                toDepHomeScreen = {
                    navController.navigate(DepannageScreens.DepannageHomeScreen.name) {
                        popUpTo(0) { inclusive = false }
                    }
                },
                toRefuse ={ navController.navigate(DepannageScreens.Refused.name){
                    popUpTo(0){
                        inclusive = false
                    }
                } },
                toProcessing = { navController.navigate(DepannageScreens.UnActivated.name){
                    popUpTo(0){
                        inclusive = false
                    }
                } },
                modifier = Modifier
            )
        }

        composable(DepannageScreens.Signup.name) {
            SignupScreen(
                onSignup = { navController.navigate(DepannageScreens.ChooseType.name) },
                onLogin = { navController.navigate(DepannageScreens.Login.name) },
                modifier = Modifier
            )
        }

        composable(DepannageScreens.HomeScreen.name) {
            DriverHomeScreen(
                onAddLoc = { navController.navigate(DepannageScreens.GetDestination.name) },
                modifier = Modifier.fillMaxSize(),
                onProfileSurfaceClick = { navController.navigate(DepannageScreens.Profile.name) },
                toPostPhoneNumber = {navController.navigate(DepannageScreens.RetrivePhoneNumber.name)},
                viewModel = hiltViewModel()
            )
        }

        composable(DepannageScreens.GetDestination.name) {
            GetDestinationScreen(
                cancelRequest = { navController.navigate(DepannageScreens.HomeScreen.name) },
                confirmRequest = { navController.navigate(DepannageScreens.HomeScreen.name) },
                viewModel = hiltViewModel()
            )
        }

        composable(DepannageScreens.CarteGrisse.name) {
            CarteGrisse(navigate = {
                navController.navigate(DepannageScreens.UnActivated.name) {
                    popUpTo(0) { inclusive = false }
                }
            })
        }

        composable(DepannageScreens.DepannageHomeScreen.name) {
            DepannageHomeScreen(
                navigateToMap = { navController.navigate(DepannageScreens.FindWork.name) },
                onEditProfileClicked = { navController.navigate(DepannageScreens.Profile.name) },
                toPostPhoneNumber = {navController.navigate(DepannageScreens.PostPhoneNumber.name)}
            )
        }

        composable(DepannageScreens.FindWork.name) {
            FindWorkScreen(
                cancelRequest = { navController.navigate(DepannageScreens.DepannageHomeScreen.name) },
                confirmRequest = { navController.navigate(DepannageScreens.DepannageHomeScreen.name) },
                viewModel = hiltViewModel()
            )
        }

        composable(DepannageScreens.ChooseType.name) {
            AccountType(
                navigateToCarteGrisse = { navController.navigate(DepannageScreens.CarteGrisse.name) },
                navigateToHome = {
                    navController.navigate(DepannageScreens.VerifyAccount.name) {
                        popUpTo(0) { inclusive = false }
                    }
                }
            )
        }

        composable(DepannageScreens.Profile.name) {
            Profile(
                viewModel = hiltViewModel(),
                onLogoutClicked = {
                    navController.navigate(DepannageScreens.Login.name) {
                        popUpTo(0) { inclusive = false }
                    }
                }
            )
        }

        composable(DepannageScreens.WaitScreen.name) {
            WaitScreen()
        }
        composable(DepannageScreens.UnActivated.name){
            UnActivated()
        }

        composable(DepannageScreens.Refused.name){
            Refused({navController.navigate(DepannageScreens.Login.name){
                popUpTo(0){
                    inclusive = false
                }
            } })
        }

        composable(DepannageScreens.PostPhoneNumber.name){
            PostPhoneNumber()
        }
        composable(DepannageScreens.RetrivePhoneNumber.name){
            RetrivePhoneNumber()
        }
        composable(DepannageScreens.VerifyAccount.name){
            VerfyAccount(
                onClientVerfied =  {navController.navigate(DepannageScreens.HomeScreen.name){
                    popUpTo(0) { inclusive = false }
                }},
                toUnActivated = {navController.navigate(DepannageScreens.UnActivated.name){
                    popUpTo(0) { inclusive = false }
                }},
                toDepHomeScreen = {navController.navigate(DepannageScreens.DepannageHomeScreen.name){
                    popUpTo(0) { inclusive = false }
                }},
                toRefused = {navController.navigate(DepannageScreens.Refused.name){
                    popUpTo(0) { inclusive = false }
                }}
            )
        }
    }
}
