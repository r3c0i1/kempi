package com.r3c0i1.kempa.features.auth.data.remote

import com.r3c0i1.kempa.features.auth.data.remote.dto.AuthByCodeRequestDto
import com.r3c0i1.kempa.features.auth.data.remote.dto.AuthRequestDto
import com.r3c0i1.kempa.features.auth.data.remote.dto.AuthResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/security/auth/auth")
    suspend fun authenticate(
        @Body request: AuthRequestDto
    ): Response<AuthResponseDto>

    @POST("api/security/auth/auth-by-code")
    suspend fun authenticateByCode(
        @Body request: AuthByCodeRequestDto
    ): Response<AuthResponseDto>
}