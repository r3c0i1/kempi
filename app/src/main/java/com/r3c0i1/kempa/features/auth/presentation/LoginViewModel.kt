package com.r3c0i1.kempa.features.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r3c0i1.kempa.features.auth.domain.model.AuthError
import com.r3c0i1.kempa.features.auth.domain.model.AuthResult
import com.r3c0i1.kempa.features.auth.domain.model.User
import com.r3c0i1.kempa.features.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val requires2FALogin: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun on2FANavigated() {
        _uiState.update { it.copy(requires2FALogin = null) }
    }

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val result = loginUseCase(login, pass)

            result.onSuccess { authResult ->
                when (authResult) {
                    is AuthResult.Success -> {
                        val user = authResult.user

                        Log.d("AUTH_CHAIN", "=== УСПЕХ ЧЕРЕЗ ДОМЕН! ===")
                        Log.d("AUTH_CHAIN", "ФИО: ${user.firstName} ${user.lastName}")
                        Log.d("AUTH_CHAIN", "Логин: ${user.login}")
                        Log.d("AUTH_CHAIN", "Тип: ${if (user is User.Teacher) "👨‍🏫 Преподаватель" else "🎓 Студент"}")


                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    
                    is AuthResult.Requires2FA -> {
                        Log.d("AUTH_CHAIN", "=== СЕРВЕР ЗАПРОСИЛ КОД 2FA ===")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                requires2FALogin = login,
                                errorMessage = null
                            )
                        }
                    }
                }
            }.onFailure { throwable ->
                Log.e("AUTH_CHAIN", "Ошибка домена", throwable)

                val message = when (throwable) {
                    is AuthError.Validation.EmptyLogin -> "Логин не может быть пустым"
                    is AuthError.Validation.EmptyPassword -> "Введите пароль"
                    is AuthError.Remote.InvalidCredentials -> "Неверный логин или пароль"
                    is AuthError.Remote.NoInternet -> "Нет интернета"
                    is AuthError.Remote.ServerError -> "Сервер КемГУ временно недоступен"
                    else -> throwable.message ?: "Ошибка авторизации"
                }

                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }
}