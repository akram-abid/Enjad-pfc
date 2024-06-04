@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.depannage.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.depannage.R
import com.example.depannage.screens.clientHomeScreen.DriverHomeViewModel
import com.example.woof.ui.theme.Typography
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

@Composable
fun Profile(
    viewModel: DriverHomeViewModel,
    onLogoutClicked: ()->Unit
){
    ProfileElemets(viewModel = viewModel, onLogoutClicked = onLogoutClicked)
}

@Composable
fun ProfileElemets(
    viewModel: DriverHomeViewModel,
    onLogoutClicked: ()->Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    var email by remember { mutableStateOf(viewModel.profileInfos.email.value) }
    var username by remember { mutableStateOf(viewModel.profileInfos.username.value) }
    var password by remember { mutableStateOf(viewModel.profileInfos.password.value) }
    var phoneNumber by remember { mutableStateOf(viewModel.profileInfos.phoneNumber.value) }
    var province by remember { mutableStateOf(viewModel.profileInfos.province.value) }
    var city by remember { mutableStateOf(viewModel.profileInfos.city.value) }
    var profilePic by remember { mutableStateOf(viewModel.profileInfos.profilePic.value) }
    var inEdit by remember { mutableStateOf(false) }
    var PicState by remember { mutableStateOf(false) }
    PicState = if (profilePic == "") false else true
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    var imageUri by remember {
        mutableStateOf("")
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUriState.value = uri
    }
    var isPickImageDialogShow by remember { mutableStateOf(false) }
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageRef: StorageReference
    val filename = UUID.randomUUID().toString() + ".jpg"
    imageRef = storageRef.child("images/$filename")
    val context = LocalContext.current

    if (isPickImageDialogShow) {
        AlertDialog(
            confirmButton = {
                TextButton(onClick = {
                    imageUriState.value?.let {
                        imageUri = it.toString()
                    }
                    imageRef.putFile(imageUri.toUri())
                        .addOnCompleteListener { task ->
                            imageRef.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    viewModel.profileInfos.updateProfilePic(task.result.toString())
                                    profilePic = task.result.toString()
                                    Toast.makeText(
                                        context,
                                        "hoaw ${task.result}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Toast.makeText(
                                        context,
                                        "hoaw ${viewModel.profileInfos.profilePic.value}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.profileInfos.updateProfilePic(task.result.toString())
                                    // Potentially handle success, e.g., show success toast
                                    Toast.makeText(
                                        context,
                                        "Carte Grise uploaded successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    db.collection("users")
                                        .document(auth.currentUser?.uid.toString())
                                        .update("profile pic", task.result)
                                        .addOnCompleteListener { taskk ->
                                            if (taskk.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "pic updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "pic updated failed",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Carte Grise upload failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    isPickImageDialogShow = false
                }) {
                    Text(text = "set the image")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isPickImageDialogShow = false
                    },
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
                ) {
                    Text(
                        text = "Cancel",
                    )
                }
            },
            title = { Text(text = "Pick profile image") },
            text = { Text(text = "dou you want to set that image as a profile picture") },
            onDismissRequest = { }
        )
    }
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(2.dp, 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "back arrow in head",
                modifier = Modifier.padding(0.dp, 0.dp, 4.dp, 0.dp)
            )
            Text(
                text = "Profile",
                style = Typography.displayLarge,
                color = MaterialTheme.colorScheme.scrim,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.weight(2f))
            var stateIcon = if (inEdit) Icons.Filled.Save else Icons.Filled.Edit
            var stateString = if (inEdit) "Save Changes" else "Edit profile"
            Button(
                onClick = {
                    if (inEdit) {
                        db.collection("users").document(auth.currentUser?.uid.toString())
                            .get().addOnSuccessListener { document ->
                                if (document.exists()) {
                                    db.collection("users")
                                        .document(auth.currentUser?.uid.toString())
                                        .delete()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "document deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "document didn't deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    val user = hashMapOf(
                                        "id" to auth.currentUser?.uid.toString(),
                                        "username" to username,
                                        "email" to email,
                                        "phone number" to phoneNumber,
                                        "profile pic" to profilePic,
                                        "province" to province,
                                        "city" to city,
                                        "password" to password,
                                    )
                                    db.collection("users")
                                        .document(auth.currentUser?.uid.toString())
                                        .set(user).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "document created",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "document didn't created",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }else{
                                    /*
                                    db.collection("workers").document(auth.currentUser?.uid.toString())
                                        .delete()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "document deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "document didn't deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }*/
                                    val user = hashMapOf(
                                        "id" to auth.currentUser?.uid.toString(),
                                        "username" to username,
                                        "email" to email,
                                        "phone number" to phoneNumber,
                                        "profile pic" to profilePic,
                                        "province" to province,
                                        "city" to city,
                                        "password" to password,
                                    )
                                    db.collection("workers").document(auth.currentUser?.uid.toString())
                                        .update(user as Map<String, Any>).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "document updated",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "document didn't created",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }
                    }
                    inEdit = !inEdit
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = stateString,
                    style = Typography.displayLarge,
                    color = MaterialTheme.colorScheme.scrim,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = stateIcon,
                    contentDescription = "back arrow in head",
                    modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp),
                    tint = MaterialTheme.colorScheme.scrim
                )
            }
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(2.dp, 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!PicState) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "profile picture",
                    modifier = Modifier
                        .width(160.dp)
                        .height(160.dp)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .clickable(inEdit, "sfs", null) {
                            launcher.launch("image/*")
                            isPickImageDialogShow = true
                        },
                    contentScale = ContentScale.Crop
                )
            } else {
                imageUriState.value?.let { imageUri ->

                }
                AsyncImage(
                    model = profilePic,
                    contentDescription = "Carte Grise",
                    modifier = Modifier
                        .width(160.dp)
                        .height(160.dp)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .clickable(inEdit, "sfs", null) {
                            launcher.launch("image/*")
                            isPickImageDialogShow = true
                        },
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.profile),
                    placeholder = painterResource(R.drawable.profile)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Text(text = "username", modifier = Modifier.padding(12.dp, 0.dp))
            }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("Enter your car year") },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                enabled = inEdit,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.1f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.scrim,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.15f)
                )
            )
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Text(text = "email", modifier = Modifier.padding(12.dp, 0.dp))
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("email") },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.scrim,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.15f)
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "province", modifier = Modifier.padding(6.dp, 0.dp))
                    OutlinedTextField(
                        value = province,
                        onValueChange = { province = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = { Text("Enter your car year") },
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        enabled = inEdit,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.1f
                            ),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            disabledTextColor = MaterialTheme.colorScheme.scrim,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.5f
                            ),
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.5f
                            ),
                            focusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.15f
                            )
                        )
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "city", modifier = Modifier
                            .padding(6.dp, 0.dp)
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = { Text("Enter your car year") },
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxWidth(),
                        enabled = inEdit,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.1f
                            ),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            disabledTextColor = MaterialTheme.colorScheme.scrim,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.5f
                            ),
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.5f
                            ),
                            focusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(
                                alpha = 0.15f
                            )
                        )
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                Text(text = "phone number", modifier = Modifier.padding(12.dp, 0.dp))
            }
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("Enter your car year") },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                enabled = inEdit,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.1f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.scrim,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.15f)
                )
            )
        }
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.padding(8.dp)
    ) {
        TextButton(
            onClick = {
            auth.signOut()
            onLogoutClicked()
        },
        ) {
            Text(
                text = "log-out",
                color = Color.Red.copy(alpha = 0.8f),
                style = Typography.displayLarge,
                fontSize = 18.sp,
                modifier = Modifier.padding(4.dp)
            )
            Icon(
                imageVector = Icons.Filled.Output,
                contentDescription = "logout icon",
                tint = Color.Red.copy(alpha = 0.8f)
            )
        }
    }
}

