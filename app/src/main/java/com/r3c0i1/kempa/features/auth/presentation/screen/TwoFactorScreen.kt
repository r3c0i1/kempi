package com.r3c0i1.kempa.features.auth.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.r3c0i1.kempa.features.auth.presentation.TwoFactorViewModel
import kotlinx.coroutines.delay

@Composable
fun TwoFactorScreen(
    onBackClick: () -> Unit,
    onSuccessAuth: () -> Unit,
    viewModel: TwoFactorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val otpState = rememberTextFieldState()

    val shakeOffset = remember { Animatable(0f) }

    val isError = state.errorMessage != null
    val isSuccess = state.isSuccess

    LaunchedEffect(otpState.text.toString()) {
        val code = otpState.text.toString()
        if (code.length == 6 && !state.isLoading && !isSuccess) {
            viewModel.submitCode(code)
        }
    }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0 using FastOutSlowInEasing
                    -20f at 50 using FastOutSlowInEasing
                    20f at 100 using FastOutSlowInEasing
                    -15f at 150 using FastOutSlowInEasing
                    15f at 200 using FastOutSlowInEasing
                    -8f at 250 using FastOutSlowInEasing
                    8f at 300 using FastOutSlowInEasing
                    -3f at 350 using FastOutSlowInEasing
                    0f at 400
                }
            )

            delay(800L)

            otpState.clearText()

            viewModel.clearError()
        }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(500)
            onSuccessAuth()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.9f))

            Icon(
                imageVector = Icons.Outlined.Mail,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Введите код",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Мы отправили вам его на почту,\nпривязанную к аккаунту",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
                    .graphicsLayer { translationX = shakeOffset.value }
            ) {
                BasicTextField(
                    state = otpState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = InputTransformation.maxLength(6),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorator = {
                        val codeText = otpState.text.toString()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(6) { index ->
                                val char = codeText.getOrNull(index)?.toString() ?: ""
                                val isCurrentCell = index == codeText.length && !isSuccess && !isError

                                val targetBorderColor = when {
                                    isSuccess -> Color(0xFF22C55E)
                                    isError -> Color(0xFFEF4444)
                                    isCurrentCell -> MaterialTheme.colorScheme.onBackground
                                    else -> MaterialTheme.colorScheme.outline
                                }

                                val animatedBorderColor by animateColorAsState(
                                    targetValue = targetBorderColor,
                                    animationSpec = tween(250),
                                    label = "cellBorder"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .height(58.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surface) // Zinc900
                                        .border(
                                            width = if (isCurrentCell || isSuccess || isError) 1.5.dp else 1.dp,
                                            color = animatedBorderColor,
                                            shape = RoundedCornerShape(14.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        color = if (isError) Color(0xFFEF4444) else MaterialTheme.colorScheme.onBackground,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (!state.canResend) {
                Text(
                    text = "Отправить снова можно через ${state.formattedTime}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            } else {
                TextButton(onClick = { viewModel.resendCode() }) {
                    Text(
                        text = "Отправить код повторно",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.3f))
        }
    }
}