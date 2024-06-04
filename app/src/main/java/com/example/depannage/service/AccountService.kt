package com.example.depannage.service

import com.example.depannage.service.User
import kotlinx.coroutines.flow.Flow

interface AccountService{
    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    suspend fun authenticate(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun createAnonymousAccount()
    suspend fun linkAccount(email: String, password: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun signup(email: String, password: String)
}