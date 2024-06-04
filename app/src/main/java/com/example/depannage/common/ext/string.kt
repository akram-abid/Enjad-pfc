package com.example.depannage.common.ext

import android.util.Patterns
import java.util.regex.Pattern
private const val MIN_PASS_LENGTH = 8

private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

private const val PHONE_NUMBER = "^0[567][0-9]{8}$"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() &&
            Patterns.EMAIL_ADDRESS.matcher(this).matches()

}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASS_LENGTH &&
            Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

fun String.isValidPhoneNumberValid(): Boolean{
    return this.isNotBlank() &&
            Pattern.compile(PHONE_NUMBER).matcher(this).matches()
}

fun String.passwordMatches(repeated: String): Boolean {
    return this == repeated
}