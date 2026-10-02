package com.r3c0i1.kempa.features.auth.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.r3c0i1.kempa.core.navigation.TwoFactorRoute
import com.r3c0i1.kempa.features.auth.domain.model.AuthError
import com.r3c0i1.kempa.features.auth.domain.usecase.Submit2FAUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TwoFactorUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val timeLeftSeconds: Int = 90
) {
    val formattedTime: String
        get() = String.format("%d:%02d", timeLeftSeconds / 60, timeLeftSeconds % 60)

    val canResend: Boolean
        get() = timeLeftSeconds == 0
}

@HiltViewModel
class TwoFactorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val submit2FAUseCase: Submit2FAUseCase
) : ViewModel() {

    private val routeData = savedStateHandle.toRoute<TwoFactorRoute>()
    val login: String = routeData.login

    private val _uiState = MutableStateFlow(TwoFactorUiState())
    val uiState: StateFlow<TwoFactorUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startResendTimer()
    }

    fun submitCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = submit2FAUseCase(login, code)

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                val message = when (throwable) {
                    is AuthError.Remote.NoInternet -> "Нет подключения к интернету"
                    is AuthError.Remote.ServerError -> "Сервер КемГУ временно недоступен"
                    else -> throwable.message ?: "Неверный код"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
            }
        }
    }

    fun resendCode() {
        if (!_uiState.value.canResend) return

        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }

            startResendTimer()

            // Тут можно дернуть повторную отправку, если бэкенд это поддерживает
        }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun startResendTimer(seconds: Int = 90) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (time in seconds downTo 0) {
                _uiState.update { it.copy(timeLeftSeconds = time) }
                delay(1000L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}