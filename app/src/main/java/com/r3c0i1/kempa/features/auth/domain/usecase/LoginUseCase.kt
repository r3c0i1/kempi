package com.r3c0i1.kempa.features.auth.domain.usecase

import com.r3c0i1.kempa.features.auth.domain.model.AuthError
import com.r3c0i1.kempa.features.auth.domain.model.AuthResult
import com.r3c0i1.kempa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String): Result<AuthResult> {
        val cleanLogin = login.trim()
        val cleanPassword = password.trim()

        if (cleanLogin.isEmpty()){
            return Result.failure(AuthError.Validation.EmptyLogin)
        }
        if (cleanPassword.isEmpty()){
            return Result.failure(AuthError.Validation.EmptyPassword)
        }

        return authRepository.login(cleanLogin, cleanPassword)
    }
}