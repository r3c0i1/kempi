package com.r3c0i1.kempa.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r3c0i1.kempa.features.auth.domain.model.AuthError
import com.r3c0i1.kempa.features.auth.domain.model.AuthResult
import com.r3c0i1.kempa.features.auth.domain.usecase.LoginUseCase
import com.r3c0i1.kempa.features.auth.domain.usecase.Submit2FAUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

enum class AuthStatus {
    SUCCESS, FAILURE, LOADING, REQUIRED_2FA, INITIAL
}

data class AuthState (
    val status: AuthStatus = AuthStatus.INITIAL,
    val errorMessage: String? = null,
    val timeLeftSeconds: Int = 90
) {
    val canResend: Boolean
        get() = timeLeftSeconds == 0

    val formattedTime: String
        get() = String.format(Locale.US, "%d:%02d", timeLeftSeconds / 60, timeLeftSeconds % 60)
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val submit2FAUseCase: Submit2FAUseCase
): ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private var inputLogin: String? = null
    private var inputPassword: String? = null

    private var timerJob: Job? = null

    fun login(login: String, password: String) {
        val cleanLogin = login.trim()
        val cleanPassword = password.trim()

        if (cleanLogin == inputLogin && !_state.value.canResend) {
            _state.update {
                it.copy(
                    status = AuthStatus.REQUIRED_2FA,
                    errorMessage = null
                )
            }
            return
        }

        if (cleanLogin != inputLogin) {
            timerJob?.cancel()
            _state.update { it.copy(timeLeftSeconds = 0) }
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    status = AuthStatus.LOADING
                )
            }

            inputLogin = cleanLogin
            inputPassword = cleanPassword

            val result = loginUseCase(cleanLogin, cleanPassword)
            result.onSuccess {
                authResult ->
                    when (authResult) {
                        is AuthResult.Success -> {
                            inputPassword = null
                            _state.update { it.copy(status = AuthStatus.SUCCESS, errorMessage = null) }
                        }
                        is AuthResult.Requires2FA -> {
                            startResendTimer()
                            _state.update {
                                it.copy(
                                    status = AuthStatus.REQUIRED_2FA,
                                    errorMessage = null
                                )
                            }
                        }
                    }
            }.onFailure { throwable ->
                val message = when (throwable) {
                    is AuthError.Validation.EmptyLogin -> "Логин не может быть пустым"
                    is AuthError.Validation.EmptyPassword -> "Введите пароль"
                    is AuthError.Remote.InvalidCredentials -> "Неверный логин или пароль"
                    is AuthError.Remote.NoInternet -> "Нет интернета"
                    is AuthError.Remote.ServerError -> "Сервер КемГУ временно недоступен"
                    else -> throwable.message ?: "Ошибка авторизации"
                }

                _state.update { it.copy(status = AuthStatus.FAILURE, errorMessage = message) }
            }
        }
    }

    fun submitCode(code: String) {
        val login = inputLogin ?: run {
            _state.update { it.copy(status = AuthStatus.FAILURE, errorMessage = "Сессия истекла. Войдите заново") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(status = AuthStatus.LOADING, errorMessage = null) }

            val result = submit2FAUseCase(login, code)

            result.onSuccess { user ->
                inputPassword = null
                timerJob?.cancel()
                _state.update {
                    it.copy(
                        status = AuthStatus.SUCCESS,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                val message = when (throwable) {
                    is AuthError.Remote.NoInternet -> "Нет подключения к интернету"
                    is AuthError.Remote.ServerError -> "Сервер временно недоступен"
                    else -> throwable.message ?: "Неверный код"
                }

                _state.update {
                    it.copy(
                        status = AuthStatus.FAILURE,
                        errorMessage = message
                    )
                }
            }
        }
    }

    fun resendCode() {
        val login = inputLogin
        val pass = inputPassword
        if (login != null && pass != null && _state.value.canResend) {
            login(login, pass)
        }
    }

    private fun startResendTimer(seconds: Int = 90) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (time in seconds downTo 0) {
                _state.update { it.copy(timeLeftSeconds = time) }
                delay(1000L.milliseconds)
            }
        }
    }

    fun resetToLogin() {
        _state.update {
            it.copy(
                status = AuthStatus.INITIAL,
                errorMessage = null
            )
        }
    }

    fun on2FANavigated() {
        _state.update { it.copy(status = AuthStatus.INITIAL, errorMessage = null) }
    }

    fun clearError() {
        if (_state.value.errorMessage != null) {
            _state.update { it.copy(errorMessage = null, status = AuthStatus.INITIAL) }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        inputLogin = null
        inputPassword = null
    }
}