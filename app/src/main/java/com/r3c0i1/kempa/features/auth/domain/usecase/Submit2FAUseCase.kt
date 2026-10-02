package com.r3c0i1.kempa.features.auth.domain.usecase

import com.r3c0i1.kempa.features.auth.domain.model.AuthError
import com.r3c0i1.kempa.features.auth.domain.model.User
import com.r3c0i1.kempa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class Submit2FAUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(login: String, code: String): Result<User> {
        val cleanLogin = login.trim()
        val cleanCode = code.trim()

        if (cleanLogin.isEmpty()) {
            return Result.failure(AuthError.Validation.EmptyLogin)
        }
        if (cleanCode.length != 6) {
            return Result.failure(AuthError.Unknown("Код должен состоять из 6 цифр"))
        }

        return repository.submit2FA(cleanLogin, cleanCode)
    }
}