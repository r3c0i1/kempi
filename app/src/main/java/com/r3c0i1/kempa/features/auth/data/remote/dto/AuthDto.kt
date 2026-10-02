package com.r3c0i1.kempa.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto (
    val login: String,
    val password: String,
    val lifetime: String = "3m"
)

@Serializable
data class AuthByCodeRequestDto (
    val login: String,
    val code: String,
    val lifetime: String = "3m"
)

@Serializable
data class AuthResponseDto (
    val success: Boolean,
    val twoFactorAuthEnabled: Boolean? = false,
    val userInfo: UserInfoDto? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val message: String? = null
)

@Serializable
data class UserInfoDto (
    val id: Long,
    val login: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val job: String? = null,
    val org: String? = null,
    val town: String? = null,
    val userType: String? = null,
    val blocked: Int = 0,
    val foreignStudFlag: Boolean = false,
    val settings: UserSettingsDto? = null,
    val twoFactorAuth: Boolean = false,
    val twoFactorAuthConfirmed: Boolean = false

)

@Serializable
data class UserSettingsDto (
    val showEmailForDirectorates: Boolean = false,
    val hideTeacherSchedule: Boolean = false
)