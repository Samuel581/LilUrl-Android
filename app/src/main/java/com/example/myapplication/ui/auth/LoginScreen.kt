package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.LilUrlApp
import com.example.myapplication.ui.components.LilUrlTextField
import com.example.myapplication.ui.components.LoadingButton
import com.example.myapplication.ui.components.PasswordTextField
import com.example.myapplication.ui.main.SmolLogo
import com.example.myapplication.util.UiState
import com.example.myapplication.util.Validation

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    successMessage: String? = null,
    onMessageShown: () -> Unit = {},
) {
    val app = LocalContext.current.applicationContext as LilUrlApp
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(app.authRepository))
    val state by viewModel.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(successMessage)
            onMessageShown()
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is UiState.Success -> onLoginSuccess()
            is UiState.Error -> snackbarHostState.showSnackbar((state as UiState.Error).message)
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(40.dp))

            SmolLogo(size = 44)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Sign in to manage your smol links.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(36.dp))

            LilUrlTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )

            Spacer(Modifier.height(12.dp))

            PasswordTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = "Password",
                errorMessage = passwordError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    attemptLogin(email, password, viewModel, { emailError = it }, { passwordError = it })
                },
                enabled = state !is UiState.Loading,
            )

            Spacer(Modifier.height(24.dp))

            LoadingButton(
                text = "Log in",
                isLoading = state is UiState.Loading,
                onClick = {
                    attemptLogin(email, password, viewModel, { emailError = it }, { passwordError = it })
                },
            )

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Don't have an account? Sign up")
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

private fun attemptLogin(
    email: String,
    password: String,
    viewModel: LoginViewModel,
    setEmailError: (String?) -> Unit,
    setPasswordError: (String?) -> Unit,
) {
    var valid = true
    if (!Validation.isValidEmail(email.trim())) {
        setEmailError("Enter a valid email address"); valid = false
    } else setEmailError(null)
    if (password.isEmpty()) {
        setPasswordError("Password is required"); valid = false
    } else setPasswordError(null)
    if (valid) viewModel.login(email.trim(), password)
}
