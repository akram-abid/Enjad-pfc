package com.example.depannage.screens.carteGrisse

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.depannage.screens.signup.SignupViewModel
import com.example.woof.ui.theme.Typography

@Composable
fun CarteGrisse(
    navigate: () -> Unit
){
    CarteGrisseElements(navigate = navigate, viewModel = hiltViewModel())
}

@Composable
fun CarteGrisseElements(
    navigate: () -> Unit,
    viewModel: SignupViewModel
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "choose your carte grisse image from gallery",
            style = Typography.displayLarge,
            fontSize = 18.sp
        )
        //Text(text = text)
        val imageState = remember { mutableStateOf<Bitmap?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            imageUriState.value = uri
        }
        Text(text = text)
        Spacer(modifier = Modifier.height(24.dp))
        Surface (
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.scrim),
            modifier = Modifier
                .height(380.dp)
                .padding(16.dp, 8.dp)
        ){
            imageUriState.value?.let { imageUri ->
                text = imageUri.toString()
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Carte Grise",
                    modifier = Modifier.fillMaxSize()
                )
/*
                imageState.value?.let { imageUrl ->
                    text = imageUrl.toString()
                    val storage = Firebase.storage
                    var storageRef = storage.reference
                    var file = Uri.fromFile(File(imageUrl.toString()))
                    val cardRef = storageRef.child("images/${file.lastPathSegment}")
                    var uploadTask = cardRef.putFile(file)
                    uploadTask.addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            Toast.makeText(context, "upload succeed", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "upload didnt succeed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "carte grisse Loaded image",
                    )
                }
*/
        }

        //LoadImage(imageState, navigate, viewModel = viewModel)
    }
        Row (
            modifier = Modifier.padding(16.dp, 4.dp)
        ){
            Button(
                //launcher.launch("image/*")
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 8.dp),

                ) {
                Text(
                    text = "choose an image",
                    style = Typography.displayLarge,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    //navigate()
                    //viewModel.registerDepAccount({ navigate() }, context)
                    viewModel.uploadCarteGrisseImage(imageUriState.value!!, context) { success ->
                        if (success) {
                            navigate()
                        } else {
                            Toast.makeText(context, "upload didnt succeed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 8.dp)
            ) {
                Text(
                    text = "send request",
                    style = Typography.displayLarge,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
}

@Composable
fun LoadImage(
    bitmap: MutableState<Bitmap?>,
    navigate: ()-> Unit,
    viewModel: SignupViewModel
) {
    val context = LocalContext.current
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    /*
    var bytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { result ->
        val item = context.contentResolver.openInputStream(result!!)
        bytes = item?.readBytes()
        item?.close()
    }
*/
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp, 4.dp)
        ) {
            Button(
                //launcher.launch("image/*")
                onClick = { },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 8.dp),

                ) {
                Text(
                    text = "choose an image",
                    style = Typography.displayLarge,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    //navigate()
                    viewModel.registerDepAccount({ navigate() }, context)
                },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp, 8.dp)
            ) {
                Text(
                    text = "send request",
                    style = Typography.displayLarge,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUriState.value = uri
    }
    Spacer(modifier = Modifier.height(24.dp))
    /*bytes?.let {
        val bos = ByteArrayOutputStream()
        val bmp = BitmapFactory.decodeByteArray(it, 0, it.size).compress(Bitmap.CompressFormat.JPEG, 50, bos)
        bitmap.value = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.toByteArray().size)
    }*/
}
}