package com.r3c0i1.kempa.features.auth.data.repository

import com.r3c0i1.kempa.features.auth.data.local.TokenStorage
import com.r3c0i1.kempa.features.auth.data.mapper.toDomainUser
import com.r3c0i1.kempa.features.auth.data.remote.AuthApiService
import com.r3c0i1.kempa.features.auth.data.remote.dto.AuthByCodeRequestDto
import com.r3c0i1.kempa.features.auth.data.remote.dto.AuthRequestDto
import com.r3c0i1.kempa.features.auth.domain.model.AuthError
import com.r3c0i1.kempa.features.auth.domain.model.AuthResult
import com.r3c0i1.kempa.features.auth.domain.model.User
import com.r3c0i1.kempa.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository  {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun login(
        login: String,
        password: String
    ): Result<AuthResult> {
        return try {
            val response = api.authenticate(
                AuthRequestDto(
                    login = login,
                    password = password
                )
            )

            if (response.isSuccessful && response.body() != null){
                val body = response.body()!!

                if (body.success) {
                    if(body.twoFactorAuthEnabled == true) {
                        return Result.success(AuthResult.Requires2FA(login))
                    }

                    val accessToken = body.accessToken ?: ""
                    val refreshToken = body.refreshToken ?: ""

                    if(accessToken.isNotBlank()){
                        tokenStorage.saveTokens(accessToken, refreshToken)
                    }

                    val userInfo = body.userInfo ?: return Result.failure(
                        AuthError.Unknown("Сервер не прислал данные профиля")
                    )
                    val user = userInfo.toDomainUser(fallbackLogin = login)
                    _currentUser.value = user

                    Result.success(AuthResult.Success(user))
                } else {
                    val message = body.message ?: "Неверный логин или пароль"
                    Result.failure(AuthError.Unknown(message))
                }
            } else {
                val error = when (response.code()) {
                    401 -> AuthError.Remote.InvalidCredentials
                    403 -> AuthError.Remote.UserBlocked
                    in 500..599 -> AuthError.Remote.ServerError(response.code())
                    else -> AuthError.Unknown("Ошибка сервера (${response.code()})")
                }
                Result.failure(error)
            }
        } catch (e: IOException) {
            Result.failure(AuthError.Remote.NoInternet)
        } catch (e: Exception) {
            Result.failure(AuthError.Unknown(e.localizedMessage ?: "Непредвиденная ошибка"))
        }
    }

    override suspend fun submit2FA(login: String, code: String): Result<User> {
        return try {
            val response = api.authenticateByCode(
                AuthByCodeRequestDto(
                    login = login,
                    code = code
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                if (body.success) {
                    val userInfo = body.userInfo ?: return Result.failure(
                        AuthError.Unknown("Сервер не прислал данные профиля")
                    )
                    val accessToken = body.accessToken ?: ""
                    val refreshToken = body.refreshToken ?: ""
                    if (accessToken.isNotBlank()) {
                        tokenStorage.saveTokens(accessToken, refreshToken)
                    }

                    val user = userInfo.toDomainUser(fallbackLogin = login)
                    _currentUser.value = user

                    Result.success(user)
                } else {
                    val errorMsg = body.message ?: "Неверный код подтверждения"
                    Result.failure(AuthError.Unknown(errorMsg))
                }
            } else {
                val error = when (response.code()) {
                    400, 401 -> AuthError.Unknown("Неверный или просроченный код")
                    403 -> AuthError.Remote.UserBlocked
                    in 500..599 -> AuthError.Remote.ServerError(response.code())
                    else -> AuthError.Unknown("Ошибка сервера: ${response.code()}")
                }
                Result.failure(error)
            }
        } catch (e: IOException) {
            Result.failure(AuthError.Remote.NoInternet)
        } catch (e: Exception) {
            Result.failure(AuthError.Unknown(e.localizedMessage ?: "Непредвиденная ошибка"))
        }
    }

    override suspend fun logout() {
        tokenStorage.clear()
        _currentUser.value = null
    }
}