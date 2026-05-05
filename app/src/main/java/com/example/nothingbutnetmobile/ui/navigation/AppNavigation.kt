package com.example.nothingbutnetmobile.ui.navigation

import androidx.compose.runtime.Composable
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
import com.example.nothingbutnetmobile.ui.screens.home.HomeScreen

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
            if (authState is AuthState.Success) {
                authViewModel.resetState()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
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
            HomeScreen()
        }
    }
}
