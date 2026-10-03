package com.r3c0i1.kempa.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ComponentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.r3c0i1.kempa.core.navigation.AuthGraph
import com.r3c0i1.kempa.core.navigation.LoginRoute
import com.r3c0i1.kempa.core.navigation.MainRoute
import com.r3c0i1.kempa.core.navigation.TwoFactorRoute
import com.r3c0i1.kempa.features.auth.presentation.screen.LoginScreen
import com.r3c0i1.kempa.features.auth.presentation.screen.TwoFactorScreen
import com.r3c0i1.kempa.features.auth.presentation.viewmodel.AuthStatus
import com.r3c0i1.kempa.features.auth.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = modifier
    ) {

        navigation<AuthGraph>(startDestination = LoginRoute){
            composable<LoginRoute> { backStackEntry ->
                val authViewModel: AuthViewModel = hiltViewModel(
                    remember(backStackEntry){
                        navController.getBackStackEntry<AuthGraph>()
                    }
                )
                LoginScreen(
                    viewModel = authViewModel,
                    onSuccessLogin = {
                        navController.navigate(MainRoute) {
                            popUpTo<LoginRoute> { inclusive = true }
                        }
                    },
                    onNavigateTo2FA = {
                        navController.navigate(TwoFactorRoute)
                    }
                )
            }

            composable<TwoFactorRoute> { backStackEntry ->
                val authViewModel: AuthViewModel = hiltViewModel(
                    remember(backStackEntry){
                        navController.getBackStackEntry<AuthGraph>()
                    }
                )

                TwoFactorScreen(
                    viewModel = authViewModel,
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