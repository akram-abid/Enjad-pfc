package com.example.depannage.screens.signup

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.depannage.screens.clientHomeScreen.Profileinfos
import com.example.depannage.screens.login.LoginUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class SignupViewModel @Inject constructor(
  private val auth: FirebaseAuth,
  val sign: GlobalSignData,
  val Dep: GlobalDepData,
  val profileInfos: Profileinfos
) : ViewModel() {
  val userID: String = auth.currentUser?.uid.toString()
  private val userCollectionRef = Firebase.firestore.collection("users")
  var uiState = mutableStateOf(LoginUiState())
    private set
  val dataBase = Firebase.firestore
  var textTepm: String = "hi"

  var isCircularIndicatorVisible = mutableStateOf(false)

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
    sign.updateEmail(newValue)
    Dep.updateEmail(newValue)
  }

  fun onUsernameChange(newValue: String){
    uiState.value = uiState.value.copy(username = newValue)
    sign.updateUsername(newValue)
    Dep.updateUsername(newValue)
  }

  fun onPhoneNumberChange(newValue: String){
    uiState.value = uiState.value.copy(phoneNumber = newValue)
    sign.updatePhoneNumber(newValue)
    Dep.updatePhoneNumber(newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
    sign.updatePassword(newValue)
    Dep.updatePassword(newValue)
  }
  fun onCarteGrisseImageChange(newValue: String) {
    Dep.updateImage(newValue)
  }

  fun onSignupClick(navigate:()-> Unit) {
    isCircularIndicatorVisible.value = true
    /*auth.createUserWithEmailAndPassword(uiState.value.email, uiState.value.password)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Log.d("cc", "createUserWithEmail:success")
          val user = auth.currentUser
          isCircularIndicatorVisible.value = false
          navigate()
        } else {
          Log.w("cc", "createUserWithEmail:failure", task.exception)
        }

      }*/
    navigate()
  }

  fun registerClientAccount(navigate: () -> Unit, context: Context){
    var registerSuccess = false
    isCircularIndicatorVisible.value = true
    auth.createUserWithEmailAndPassword(sign.email.value, sign.password.value)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          auth.currentUser?.sendEmailVerification()
          Toast.makeText(context, "sucsess compte", Toast.LENGTH_SHORT).show()
          isCircularIndicatorVisible.value = false

          val usercon = hashMapOf(
            "id" to auth.currentUser?.uid,
            "username" to sign.username.value,
            "email" to sign.email.value,
            "phone number" to sign.phoneNumber.value,
            "profile pic" to "",
            "province" to "",
            "city" to "",
            "password" to sign.password.value,
          )
//   a new document with a generated ID
          dataBase.collection("users")
            .document(auth.currentUser?.uid.toString())
            .set(usercon)
            .addOnSuccessListener { documentReference ->
              Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
              Toast.makeText(context, "sucsess database", Toast.LENGTH_SHORT).show()
              navigate()
            }
            .addOnFailureListener { e ->
              Log.w("TAG", "Error adding document", e)
              Toast.makeText(context, "fail data", Toast.LENGTH_SHORT).show()
            }
        } else {
          Log.w("cc", "createUserWithEmail:failure", task.exception)
        }
      }
  }

  fun registerDepAccount(navigate: () -> Unit, context: Context){
    isCircularIndicatorVisible.value = true
    auth.createUserWithEmailAndPassword(sign.email.value, sign.password.value)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          auth.currentUser?.sendEmailVerification()
          Log.d("cc", "createUserWithEmail:success")
          Toast.makeText(context, "sucsess compte", Toast.LENGTH_SHORT).show()
          isCircularIndicatorVisible.value = false
          val usercon = hashMapOf(
            "id" to auth.currentUser?.uid.toString(),
            "username" to Dep.username.value,
            "email" to Dep.email.value,
            "phone number" to Dep.phoneNumber.value,
            "province" to "",
            "city" to "",
            "profile pic" to "",
            "password" to Dep.password.value,
            "image" to Dep.image.value
          )

          dataBase.collection("workers")
            .document(userID)
            .set(usercon)
            .addOnSuccessListener { documentReference ->
              Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
              Toast.makeText(context, "sucsess database", Toast.LENGTH_SHORT).show()

              navigate()
            }
            .addOnFailureListener { e ->
              Log.w("TAG", "Error adding document", e)
              Toast.makeText(context, "fail data", Toast.LENGTH_SHORT).show()
            }
        } else {
          Log.w("cc", "createUserWithEmail:failure", task.exception)
        }

      }
    navigate()
  }

  fun signOut(){
    auth.signOut()
  }
  fun uploadCarteGrisseImage(
    imageUri: Uri,
    context: Context,
    callback: (success: Boolean) -> Unit
  ) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageRef: StorageReference

    // Generate a unique filename using UUID
    val filename = UUID.randomUUID().toString() + ".jpg"
    imageRef = storageRef.child("images/$filename")

    imageRef.putFile(imageUri)
      .addOnCompleteListener { task ->
        //onCarteGrisseImageChange(imageRef.downloadUrl.result.toString())
        //textTepm = imageRef.downloadUrl.result.toString()
        imageRef.downloadUrl.addOnCompleteListener { task->
          if (task.isSuccessful){
            Dep.updateImage(task.result.toString())
            Toast.makeText(context, "hoaw ${task.result}", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "hoaw ${Dep.image.value}", Toast.LENGTH_SHORT).show()
            textTepm = Dep.image.value

            auth.createUserWithEmailAndPassword(sign.email.value, sign.password.value)
              .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                  Log.d("cc", "createUserWithEmail:success")
                  Toast.makeText(context, "sucsess compte", Toast.LENGTH_SHORT).show()
                  isCircularIndicatorVisible.value = false
                  val usercon = hashMapOf(
                    "username" to Dep.username.value,
                    "email" to Dep.email.value,
                    "password" to Dep.password.value,
                    "phone number" to Dep.phoneNumber.value,
                    "province" to "",
                    "city" to "",
                    "profile pic" to "",
                    "account state" to "unactivated",
                    "image" to Dep.image.value
                  )

                  dataBase.collection("workers").document(auth.currentUser?.uid.toString())
                    .set(usercon)
                    .addOnSuccessListener { documentReference ->
                      Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
                      Toast.makeText(context, "sucsess database", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                      Log.w("TAG", "Error adding document", e)
                      Toast.makeText(context, "fail data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                  Log.w("cc", "createUserWithEmail:failure", task.exception)
                }
              }
          }
        }
        if (task.isSuccessful) {
          callback(true)
          // Potentially handle success, e.g., show success toast
          Toast.makeText(context, "Carte Grise uploaded successfully", Toast.LENGTH_SHORT).show()
        } else {
          callback(false)
          // Handle upload failure gracefully
          Toast.makeText(context, "Carte Grise upload failed", Toast.LENGTH_SHORT).show()
        }
      }
  }
}


  /*
  fun onSignInClick(openAndPopUp: (String, String) -> Unit) {


    launchCatching {
      accountService.authenticate(email, password)
      openAndPopUp(SETTINGS_SCREEN, LOGIN_SCREEN)
    }
  }

  fun onForgotPasswordClick() {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    launchCatching {
      accountService.sendRecoveryEmail(email)
      SnackbarManager.showMessage(AppText.recovery_email_sent)
    }
  }*/

