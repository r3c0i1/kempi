package com.r3c0i1.kempa.features.auth.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r3c0i1.kempa.R

@Composable
fun AuthHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.kempi_hello),
            contentDescription = "Кемпи",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Добро пожаловать!",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
    }
}