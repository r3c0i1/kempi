package com.r3c0i1.kempa.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.r3c0i1.kempa.core.navigation.LoginRoute
import com.r3c0i1.kempa.core.navigation.MainRoute
import com.r3c0i1.kempa.core.navigation.TwoFactorRoute
import com.r3c0i1.kempa.features.auth.presentation.screen.LoginScreen
import com.r3c0i1.kempa.features.auth.presentation.screen.TwoFactorScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = LoginRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable<LoginRoute> {
            LoginScreen(
                onSuccessLogin = {
                    navController.navigate(MainRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateTo2FA = { login ->
                    navController.navigate(TwoFactorRoute(login = login))
                }
            )
        }

        composable<TwoFactorRoute> { backStackEntry ->
            val route: TwoFactorRoute = backStackEntry.toRoute()

            TwoFactorScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccessAuth = {
                    navController.navigate(MainRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<MainRoute> {
            Column() {
                Text(
                    "Main!"
                )
            }
        }
    }
}