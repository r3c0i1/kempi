package com.r3c0i1.kempa.features.auth.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.r3c0i1.kempa.features.auth.presentation.LoginUiState
import com.r3c0i1.kempa.features.auth.presentation.LoginViewModel
import com.r3c0i1.kempa.features.auth.presentation.component.BouncingDotsIndicator
import com.r3c0i1.kempa.features.auth.presentation.component.AuthHeader
import com.r3c0i1.kempa.features.auth.presentation.component.AuthInputCard
import com.r3c0i1.kempa.features.auth.presentation.component.ErrorBanner

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onSuccessLogin: () -> Unit = {},
    onNavigateTo2FA: (login: String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSuccessLogin()
        }
    }

    LaunchedEffect(state.requires2FALogin) {
        state.requires2FALogin?.let { login ->
            onNavigateTo2FA(login)
            viewModel.on2FANavigated()
        }
    }

    LoginContent(
        state = state,
        onLoginSubmit = { login, pass -> viewModel.login(login, pass) }
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    onLoginSubmit: (String, String) -> Unit
) {
    val loginState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()

    fun submit() {
        onLoginSubmit(
            loginState.text.toString().trim(),
            passwordState.text.toString()
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AuthHeader()

            Spacer(modifier = Modifier.height(28.dp))

            AuthInputCard(
                loginState = loginState,
                passwordState = passwordState,
                onDone = ::submit
            )

            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(animationSpec = tween(250)) + expandVertically(animationSpec = tween(250)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200)),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                ErrorBanner(message = state.errorMessage ?: "")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = ::submit,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .height(54.dp),
                shape = CircleShape
            ) {
                AnimatedContent(
                    targetState = state.isLoading,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    },
                    label = "ButtonContentAnimation"
                ) { isLoading ->
                    if (isLoading) {
                        BouncingDotsIndicator()
                    } else {
                        Text(
                            text = "Войти",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1.3f))
        }
    }
}