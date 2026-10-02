package com.r3c0i1.kempa.features.auth.domain.model

sealed class AuthError : Throwable() {
    sealed class Validation : AuthError() {
        data object EmptyLogin : Validation()
        data object EmptyPassword : Validation()
    }

    sealed class Remote : AuthError() {
        data object InvalidCredentials : Remote()
        data object UserBlocked : Remote()
        data object NoInternet : Remote()
        data class ServerError(val code: Int) : Remote()
    }

    data class Unknown(override val message: String) : AuthError()
}