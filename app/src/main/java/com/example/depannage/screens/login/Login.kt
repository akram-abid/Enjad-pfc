package com.example.depannage.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.depannage.common.ext.isValidEmail
import com.example.depannage.common.ext.isValidPassword
import com.example.woof.ui.theme.Shapes
import com.example.woof.ui.theme.Typography

@Composable
fun LoginScreen(
    toDriverHomeScreen: ()-> Unit,
    toDepHomeScreen: ()-> Unit,
    onCreateAccount:()-> Unit,
    toRefuse:()-> Unit,
    toProcessing:()-> Unit,
    modifier: Modifier,
)
{

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        LoginElements(
            toDriverHomeScreen = toDriverHomeScreen,
            toDepHomeScreen = toDepHomeScreen,
            onCreateAccountClick = onCreateAccount,
            toRefuse = toRefuse,
            toProcessing = toProcessing,
            modifier = modifier
        )
    }
}

@Composable
fun LoginElements(
    toDriverHomeScreen: () -> Unit,
    toDepHomeScreen: ()-> Unit,
    onCreateAccountClick: () -> Unit,
    toRefuse:()-> Unit,
    toProcessing:()-> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isUsernameValid by remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(false) }
    var isUsernameErrorTextShow by remember { mutableStateOf(false) }
    var isPasswordErrorTextShow by remember { mutableStateOf(false) }


    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .wrapContentSize()
        ) {
            Text(
                text = "Welcome Back",
                style = Typography.bodyLarge,
                fontSize = 32.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "we are happy to see you here again. Enter your Email and Password",
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
                    viewModel.onEmailChange(username)
                    isUsernameValid = !username.isValidEmail()
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
                placeholder = { Text("Enter Username") }
                )
            if (!isUsernameValid) isUsernameErrorTextShow = false
            if (isUsernameErrorTextShow) {
                Text(
                    text = "Invalid username",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter Password") },
                shape = Shapes.medium,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )
            if (!isPasswordValid) isPasswordErrorTextShow = false
            if (isPasswordErrorTextShow) {
                Text(
                    text = "password should contain 8 characters, one uppercase and number",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.padding(6.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isUsernameValid = !username.isValidEmail()
                        isPasswordValid = !password.isValidPassword()
                        isUsernameErrorTextShow = !username.isValidEmail()
                        isPasswordErrorTextShow = !password.isValidPassword()

                        if (!isUsernameValid && !isPasswordValid) {
                            viewModel.onSigninClick(toDriverHomeScreen, toDepHomeScreen, toRefuse, toProcessing)
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = Shapes.medium
                ) {
                    Text(
                        text = "Sign in",
                        style = Typography.displayLarge,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Text(
                    text = "forgot password?",
                    style = Typography.displayLarge,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(12.dp)
                )
                Text(
                    text = "or",
                    style = Typography.displayMedium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(4.dp)
                        .alpha(0.5f)
                )
                OutlinedButton(
                    onClick = onCreateAccountClick,
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    shape = Shapes.medium
                ) {
                    Text(
                        text = "create an account",
                        style = Typography.displayLarge,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        if(viewModel.isCircularIndicatorVisible.value){
            CircularProgressIndicator()
        }
    }
}
//}
/*
@Composable
@Preview(showBackground = true)
fun LoginCardPreview() {
    LoginElements(
        Modifier,
        onLoginClick = {am,ckd->},
        onCreateAccountClick = {->}
}*/