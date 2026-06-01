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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.SmolifyApp
import com.example.myapplication.ui.components.SmolifyTextField
import com.example.myapplication.ui.components.LoadingButton
import com.example.myapplication.ui.components.PasswordTextField
import com.example.myapplication.ui.main.SmolifyLogo
import com.example.myapplication.ui.theme.SmolifyTheme
import com.example.myapplication.util.UiState
import com.example.myapplication.util.Validation

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as SmolifyApp
    val viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.factory(app.authRepository))
    val state by viewModel.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        when (state) {
            is UiState.Success -> onRegisterSuccess()
            is UiState.Error -> snackbarHostState.showSnackbar((state as UiState.Error).message)
            else -> Unit
        }
    }

    RegisterScreenContent(
        email = email,
        emailError = emailError,
        password = password,
        passwordError = passwordError,
        confirm = confirm,
        confirmError = confirmError,
        isLoading = state is UiState.Loading,
        snackbarHostState = snackbarHostState,
        onEmailChange = { email = it; emailError = null },
        onPasswordChange = { password = it; passwordError = null },
        onConfirmChange = { confirm = it; confirmError = null },
        onRegister = {
            attemptRegister(
                email, password, confirm, viewModel,
                { emailError = it }, { passwordError = it }, { confirmError = it },
            )
        },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun RegisterScreenContent(
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    confirm: String,
    confirmError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
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

            SmolifyLogo(size = 44)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Create your account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Shorten, share, and track in one tap.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(32.dp))

            SmolifyTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )

            Spacer(Modifier.height(12.dp))

            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                errorMessage = passwordError,
                supportingText = if (passwordError == null) "At least 8 characters" else null,
                imeAction = ImeAction.Next,
            )

            Spacer(Modifier.height(12.dp))

            PasswordTextField(
                value = confirm,
                onValueChange = onConfirmChange,
                label = "Confirm password",
                errorMessage = confirmError,
                imeAction = ImeAction.Done,
                onImeAction = onRegister,
            )

            Spacer(Modifier.height(24.dp))

            LoadingButton(
                text = "Create account",
                isLoading = isLoading,
                onClick = onRegister,
            )

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Already have an account? Log in")
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

private fun attemptRegister(
    email: String,
    password: String,
    confirm: String,
    viewModel: RegisterViewModel,
    setEmailError: (String?) -> Unit,
    setPasswordError: (String?) -> Unit,
    setConfirmError: (String?) -> Unit,
) {
    var valid = true
    if (!Validation.isValidEmail(email.trim())) {
        setEmailError("Enter a valid email address"); valid = false
    } else setEmailError(null)
    if (!Validation.isValidPassword(password)) {
        setPasswordError("Password must be at least 8 characters"); valid = false
    } else setPasswordError(null)
    if (password != confirm) {
        setConfirmError("Passwords don't match"); valid = false
    } else setConfirmError(null)
    if (valid) viewModel.register(email.trim(), password)
}

@Preview(showBackground = true, name = "Register – idle")
@Composable
private fun RegisterScreenIdlePreview() {
    SmolifyTheme(darkTheme = false, dynamicColor = false) {
        RegisterScreenContent(
            email = "",
            emailError = null,
            password = "",
            passwordError = null,
            confirm = "",
            confirmError = null,
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onRegister = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Register – validation errors")
@Composable
private fun RegisterScreenErrorPreview() {
    SmolifyTheme(darkTheme = false, dynamicColor = false) {
        RegisterScreenContent(
            email = "bad",
            emailError = "Enter a valid email address",
            password = "123",
            passwordError = "Password must be at least 8 characters",
            confirm = "456",
            confirmError = "Passwords don't match",
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onRegister = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Register – loading")
@Composable
private fun RegisterScreenLoadingPreview() {
    SmolifyTheme(darkTheme = false, dynamicColor = false) {
        RegisterScreenContent(
            email = "user@example.com",
            emailError = null,
            password = "password123",
            passwordError = null,
            confirm = "password123",
            confirmError = null,
            isLoading = true,
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onRegister = {},
            onNavigateBack = {},
        )
    }
}
