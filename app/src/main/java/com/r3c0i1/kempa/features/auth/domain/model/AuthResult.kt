package com.r3c0i1.kempa.features.auth.domain.model

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult

    data class Requires2FA(
        val login: String
    ) : AuthResult
}