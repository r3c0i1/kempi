package com.r3c0i1.kempa.features.auth.domain.usecase

import com.r3c0i1.kempa.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}