package com.example.myapplication.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.LilUrlApp
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.links.LinksScreen
import com.example.myapplication.ui.result.ResultScreen
import androidx.compose.ui.platform.LocalContext

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val LINKS = "links"
    const val RESULT = "result/{shortUrl}/{shortCode}/{originalUrl}?expiresAt={expiresAt}"

    fun resultRoute(
        shortUrl: String,
        shortCode: String,
        originalUrl: String,
        expiresAt: String?,
    ): String {
        val base = "result/${Uri.encode(shortUrl)}/${Uri.encode(shortCode)}/${Uri.encode(originalUrl)}"
        return if (expiresAt != null) "$base?expiresAt=${Uri.encode(expiresAt)}" else base
    }
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as LilUrlApp
    val start = if (app.authStorage.isLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = start) {

        composable(Routes.LOGIN) { backStackEntry ->
            val successMessage by backStackEntry.savedStateHandle
                .getStateFlow<String?>("successMessage", null)
                .collectAsStateWithLifecycle()
            LoginScreen(
                successMessage = successMessage,
                onMessageShown = { backStackEntry.savedStateHandle.remove<String>("successMessage") },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
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

        composable(Routes.HOME) {
            HomeScreen(
                onShortenSuccess = { shortUrl, shortCode, originalUrl, expiresAt ->
                    navController.navigate(Routes.resultRoute(shortUrl, shortCode, originalUrl, expiresAt))
                },
                onNavigateToLinks = { navController.navigate(Routes.LINKS) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onSessionExpired = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.LINKS) {
            LinksScreen(
                onNavigateBack = { navController.popBackStack() },
                onSessionExpired = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(
                navArgument("shortUrl") { type = NavType.StringType },
                navArgument("shortCode") { type = NavType.StringType },
                navArgument("originalUrl") { type = NavType.StringType },
                navArgument("expiresAt") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { entry ->
            ResultScreen(
                shortUrl = Uri.decode(entry.arguments?.getString("shortUrl").orEmpty()),
                shortCode = Uri.decode(entry.arguments?.getString("shortCode").orEmpty()),
                originalUrl = Uri.decode(entry.arguments?.getString("originalUrl").orEmpty()),
                expiresAt = entry.arguments?.getString("expiresAt")?.let { Uri.decode(it) },
                onShortenAnother = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
            )
        }
    }
}
