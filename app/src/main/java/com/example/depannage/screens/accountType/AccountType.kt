package com.example.depannage.screens.accountType

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.depannage.R
import com.example.depannage.screens.signup.SignupViewModel
import com.example.woof.ui.theme.Typography

@Composable
fun AccountType(
    navigateToHome: () -> Unit,
    navigateToCarteGrisse: ()-> Unit
){
    AccountTypeElements(
        modifier = Modifier,
        viewModel = hiltViewModel(),
        navigateToHome = navigateToHome,
        navigateToCarteGrisse = navigateToCarteGrisse
    )
}

@Composable
fun AccountTypeElements(
    modifier: Modifier,
    viewModel: SignupViewModel,
    navigateToHome:()-> Unit,
    navigateToCarteGrisse: ()-> Unit
){
    var clientButton by remember { mutableStateOf(false) }
    var towTruckButton by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "Choose your account type",
            fontSize = 24.sp,
            style = Typography.displayLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(42.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ){
            Button(
                onClick = {
                    towTruckButton = true
                    clientButton = false
                          },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(0.dp, 0.dp, 8.dp, 0.dp)
                    .border(
                        width = 2.dp,
                        color = if (towTruckButton) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(22.dp)
                    )
            )
            {
                Column(
                    modifier = Modifier.padding(6.dp, 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.types_01),
                        contentDescription = "depannage",
                        modifier = Modifier.padding(0.dp, 14.dp,0.dp,0.dp)
                    )
                    Spacer(modifier = Modifier.padding(12.dp))
                    Text(
                        text = "TowTruck",
                        style = Typography.bodyLarge,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 8.dp),
                        color = MaterialTheme.colorScheme.scrim
                    )
                }
            }
            Button(
                onClick = {
                    clientButton = true
                    towTruckButton = false
                          },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 0.dp, 0.dp, 0.dp)
                    .border(
                        width = 2.dp,
                        color = if (clientButton) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(22.dp)
                    )
            )
            {
                Column(
                    modifier = Modifier.padding(6.dp, 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.types_02),
                        contentDescription = "client",
                        modifier = Modifier.padding(0.dp, 14.dp,0.dp,0.dp)
                    )
                    Spacer(modifier = Modifier.padding(12.dp))
                    Text(
                        text = "client",
                        style = Typography.bodyLarge,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(2.dp, 2.dp, 2.dp, 8.dp),
                        color = MaterialTheme.colorScheme.scrim
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(42.dp))
        Button(
            onClick = {
                if (clientButton) {
                    viewModel.registerClientAccount(context = context, navigate = navigateToHome)
                }else{
                    navigateToCarteGrisse()
                }
            },
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = "continue",
                style = Typography.displayLarge,
                fontSize = 18.sp,
                modifier = Modifier.padding(6.dp),
                color = MaterialTheme.colorScheme.scrim
            )
        }
        //Text(text = viewModel.sign.username.value)
        //Text(text = viewModel.sign.email.value)
        //Text(text = viewModel.sign.password.value)
    }
}