package com.exam.me.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exam.me.ui.auth.LoginScreen
import com.exam.me.ui.auth.RegisterScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(onNavigateToRegister = { navController.navigate(Screen.Register.route) })
        }
        composable(Screen.Register.route) {
            RegisterScreen(onNavigateToLogin = { navController.navigate(Screen.Login.route) })
        }
    }
}
