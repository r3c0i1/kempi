package com.r3c0i1.kempa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.r3c0i1.kempa.navigation.AppNavHost
import com.r3c0i1.kempa.ui.theme.KempaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KempaTheme() {
                AppNavHost()
            }
        }
    }
}