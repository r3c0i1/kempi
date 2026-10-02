package com.r3c0i1.kempa.features.auth.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.10f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )
    }
}