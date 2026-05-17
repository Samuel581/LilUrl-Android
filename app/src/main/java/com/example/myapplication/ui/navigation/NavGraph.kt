package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.LilUrlApp
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.main.MainScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as LilUrlApp
    val start = if (app.authStorage.isLoggedIn()) Routes.MAIN else Routes.LOGIN

    NavHost(navController = navController, startDestination = start) {

        composable(Routes.LOGIN) { backStackEntry ->
            val successMessage by backStackEntry.savedStateHandle
                .getStateFlow<String?>("successMessage", null)
                .collectAsStateWithLifecycle()
            LoginScreen(
                successMessage = successMessage,
                onMessageShown = { backStackEntry.savedStateHandle.remove<String>("successMessage") },
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle?.set("successMessage", "Account created! Please log in.")
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }
    }
}
