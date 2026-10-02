package com.r3c0i1.kempa.features.auth.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthInputCard(
    loginState: TextFieldState,
    passwordState: TextFieldState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val inputTextStyle = TextStyle(
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
    ) {
        BasicTextField(
            state = loginState,
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = Modifier.fillMaxWidth(),
            textStyle = inputTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            onKeyboardAction = {
                focusManager.moveFocus(FocusDirection.Down)
            },
            decorator = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (loginState.text.isEmpty()) {
                            Text(
                                text = "Логин",
                                style = inputTextStyle.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )

        BasicSecureTextField(
            state = passwordState,
            textObfuscationMode = if (isPasswordVisible) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.Hidden
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = inputTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            onKeyboardAction = {
                focusManager.clearFocus()
                onDone()
            },
            decorator = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 16.dp)
                    ) {
                        if (passwordState.text.isEmpty()) {
                            Text(
                                text = "Пароль",
                                style = inputTextStyle.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        innerTextField()
                    }
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        )
    }
}