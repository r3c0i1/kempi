package com.r3c0i1.kempa.features.auth.domain.repository

import com.r3c0i1.kempa.features.auth.domain.model.AuthResult
import com.r3c0i1.kempa.features.auth.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(login: String, password: String): Result<AuthResult>
    suspend fun submit2FA(login: String, code: String): Result<User>
    suspend fun logout()
}