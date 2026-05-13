package com.example.nothingbutnetmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nothingbutnetmobile.data.local.TokenManager
import com.example.nothingbutnetmobile.ui.screens.auth.AuthState
import com.example.nothingbutnetmobile.ui.screens.auth.AuthViewModel
import com.example.nothingbutnetmobile.ui.screens.auth.LoginScreen
import com.example.nothingbutnetmobile.ui.screens.auth.RegisterScreen
import com.example.nothingbutnetmobile.ui.screens.home.HomeScreen
import com.example.nothingbutnetmobile.ui.screens.analysis.AnalysisScreen
import com.example.nothingbutnetmobile.ui.screens.history.HistoryScreen
import com.example.nothingbutnetmobile.ui.screens.profile.ProfileScreen
import com.example.nothingbutnetmobile.ui.screens.record.RecordScreen

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            com.example.nothingbutnetmobile.ui.screens.splash.LoadingScreen(
                onTimeout = {
                    val destination = if (tokenManager.isLoggedIn()) "home" else "login"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()

            // Navigate to home upon successful login
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    authViewModel.resetState()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            LoginScreen(
                authState = authState,
                onLoginClick = { username, password ->
                    authViewModel.login(username, password)
                },
                onRegisterClick = {
                    authViewModel.resetState()
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                if (authState is AuthState.RegisterSuccess) {
                    // Navigate to login after successful registration
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                authState = authState,
                onRegisterClick = { username, email, password ->
                    authViewModel.register(username, email, password)
                },
                onLoginClick = {
                    authViewModel.resetState()
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("record") {
            RecordScreen(navController = navController)
        }

        composable(
            route = "analysis?videoUri={videoUri}",
            arguments = listOf(
                androidx.navigation.navArgument("videoUri") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val videoUri = backStackEntry.arguments?.getString("videoUri")
            AnalysisScreen(navController = navController, videoUri = videoUri)
        }

        composable("history") {
            HistoryScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}
