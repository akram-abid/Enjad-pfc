package com.example.depannage.screens.signup

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.depannage.common.ext.isValidEmail
import com.example.depannage.common.ext.isValidPassword
import com.example.depannage.common.ext.isValidPhoneNumberValid
import com.example.depannage.common.ext.passwordMatches
import com.example.depannage.ui.theme.md_theme_light_error
import com.example.woof.ui.theme.Shapes
import com.example.woof.ui.theme.Typography
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Composable
fun SignupScreen(
    onSignup: ()-> Unit,
    onLogin: () -> Unit,
    modifier: Modifier,

){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        SignupElements(
            onLogin = onLogin,
            onSignup = onSignup,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SignupElements(
    //onSignup: KFunction2<String, String, Unit>,
    onSignup: ()-> Unit,
    onLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        //modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .wrapContentSize()
        ) {
            val db = Firebase.firestore
            var email by remember { mutableStateOf(("")) }
            var username by remember { mutableStateOf("") }
            var phoneNumber by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }
            var isUsernameValid by remember { mutableStateOf(false) }
            var isPasswordValid by remember { mutableStateOf(false) }
            var isPhoneNumberValid by remember{ mutableStateOf(false) }
            var isUsernameErrorTextShow by remember { mutableStateOf(false) }
            var isPasswordErrorTextShow by remember { mutableStateOf(false) }
            var isPhoneNumberErrorTextShow by remember { mutableStateOf(false) }
            var isConfirmPasswordError by remember { mutableStateOf(false) }
            var isConfirmPasswordErrorTextShow by remember { mutableStateOf(false) }
            val context = LocalContext.current
            Text(
                text = "Create an account",
                style = Typography.bodyLarge,
                fontSize = 32.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "create your account, it takes less than one minute.",
                style = Typography.displayMedium,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(2.dp)
                    .alpha(0.5f)
            )
            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.onUsernameChange(username)
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                label = { Text("Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Username"
                    )
                },
                shape = Shapes.medium,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Username") },
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.onEmailChange(email)
                    isUsernameValid = !email.isValidEmail()
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                isError = isUsernameValid,
                label = { Text("Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Username"
                    )
                },
                shape = Shapes.medium,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter email") },
            )
            if (!isUsernameValid) isUsernameErrorTextShow = false
            if (isUsernameErrorTextShow) {
                Text(
                    text = "Invalid username",
                    fontSize = 12.sp,
                    color = md_theme_light_error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    viewModel.onPhoneNumberChange(phoneNumber)
                    isPhoneNumberValid = !phoneNumber.isValidPhoneNumberValid()
                },
                isError = isPhoneNumberValid,
                label = { Text("phone number") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Phone number"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Password") },
                shape = Shapes.medium,
                singleLine = true,
            )
            if (!isPhoneNumberValid) isPhoneNumberErrorTextShow = false
            if (isPhoneNumberErrorTextShow) {
                Text(
                    text = "Invalid Phone number",
                    fontSize = 12.sp,
                    color = md_theme_light_error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                thickness = 2.dp,
                modifier = Modifier.padding(50.dp, 6.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.onPasswordChange(password)
                    isPasswordValid = !password.isValidPassword()
                },
                isError = isPasswordValid,
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Password") },
                shape = Shapes.medium,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )
            if (!isPasswordValid) isPasswordErrorTextShow = false
            if (isPasswordErrorTextShow) {
                Text(
                    text = "Invalid password",
                    fontSize = 12.sp,
                    color = md_theme_light_error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isConfirmPasswordError = !confirmPassword.passwordMatches(password)
                },
                isError = isConfirmPasswordError,
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Password") },
                shape = Shapes.medium,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )
            if (confirmPassword.passwordMatches(password)) isConfirmPasswordErrorTextShow = false
            if (isConfirmPasswordErrorTextShow) {
                Text(
                    text = "you made mistake retyping the password",
                    fontSize = 12.sp,
                    color = md_theme_light_error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isUsernameValid = !email.isValidEmail()
                        isPasswordValid = !password.isValidPassword()
                        isUsernameErrorTextShow = !email.isValidEmail()
                        isPasswordErrorTextShow = !password.isValidPassword()
                        isPhoneNumberValid = !phoneNumber.isValidPhoneNumberValid()
                        isPhoneNumberErrorTextShow = !phoneNumber.isValidPhoneNumberValid()
                        isConfirmPasswordError = !confirmPassword.passwordMatches(password)
                        isConfirmPasswordErrorTextShow = isConfirmPasswordError

                        if (!isUsernameValid &&
                            !isPasswordValid &&
                            !isPhoneNumberValid &&
                            !isConfirmPasswordError
                            ) {
                            viewModel.onSignupClick({ onSignup() })
                            /*
                            val user = hashMapOf(
                                "username" to viewModel.uiState.value.username,
                                "email" to viewModel.uiState.value.email,
                                "password" to viewModel.uiState.value.password,
                            )
*/
// Add a new document with a generated ID
                            /*
                            db.collection("users")
                                .add(user)
                                .addOnSuccessListener { documentReference ->
                                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                                    Toast.makeText(context, "sucsess", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("TAG", "Error adding document", e)
                                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
                                }*/
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = Shapes.medium
                ) {

                    Text(
                        text = "Create an account",
                        style = Typography.displayLarge,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "or",
                        style = Typography.displayMedium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .alpha(0.5f)
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account ?",
                        style = Typography.displayLarge,
                        color = MaterialTheme.colorScheme.scrim,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = onLogin) {
                        Text(
                            text = "Log in",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }
    }
    if (viewModel.isCircularIndicatorVisible.value) {
        CircularProgressIndicator()
    }
}

fun addUser(user :DBUser, db: Firebase , context: Context) = CoroutineScope(Dispatchers.IO).launch {

    val user = hashMapOf(
        "first" to "Ada",
        "last" to "Lovelace",
        "born" to 1815,
    )
val dbb = db.firestore
// Add a new document with a generated ID
    dbb.collection("users")
        .add(user)
        .addOnSuccessListener { documentReference ->
            Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            Toast.makeText(context, "sucsess", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Log.w("TAG", "Error adding document", e)
            Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
        }
}

data class DBUser(
    val email: String,
    val password: String,
    val fullName: String
)

@Singleton
class GlobalSignData @Inject constructor(){
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    fun updateEmail(newLocation: String){
        _email.value = newLocation
    }
    fun updatePhoneNumber(newLocation: String){
        _phoneNumber.value = newLocation
    }
    fun updatePassword(newLocation: String){
        _password.value = newLocation
    }
    fun updateUsername(newLocation: String){
        _username.value = newLocation
    }
}

@Singleton
class GlobalDepData @Inject constructor(){
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    private val _image = MutableStateFlow("")
    val image = _image.asStateFlow()

    fun updateEmail(newLocation: String){
        _email.value = newLocation
    }
    fun updatePassword(newLocation: String){
        _password.value = newLocation
    }
    fun updatePhoneNumber(newLocation: String){
        _phoneNumber.value = newLocation
    }
    fun updateUsername(newLocation: String){
        _username.value = newLocation
    }
    fun updateImage(newValue: String){
        _image.value = newValue
    }
}
/*
@Preview(showBackground = true)
@Composable
fun SignupElementsPreview(){
    DepannageTheme {

        SignupElements(onLogin = {su,iz->}, modifier = Modifier.fillMaxSize())
    }
}*/
