package com.example.myapplication.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.LilUrlApp
import com.example.myapplication.data.remote.model.ShortenResponse
import com.example.myapplication.ui.components.LilUrlTextField
import com.example.myapplication.ui.components.LoadingButton
import com.example.myapplication.util.UiState
import com.example.myapplication.util.Validation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onShortenSuccess: (shortUrl: String, shortCode: String, originalUrl: String, expiresAt: String?) -> Unit,
    onNavigateToLinks: () -> Unit,
    onLogout: () -> Unit,
    onSessionExpired: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as LilUrlApp
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory(app.urlRepository, app.authRepository)
    )
    val shortenState by viewModel.shortenState.collectAsStateWithLifecycle()
    val logoutState by viewModel.logoutState.collectAsStateWithLifecycle()

    var url by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var expiresAtDisplay by remember { mutableStateOf("") }
    var expiresAtIso by remember { mutableStateOf<String?>(null) }
    var urlError by remember { mutableStateOf<String?>(null) }
    var aliasError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(shortenState) {
        when (val s = shortenState) {
            is UiState.Success -> {
                @Suppress("UNCHECKED_CAST")
                val result = (s as UiState.Success<ShortenResponse>).data
                viewModel.resetShortenState()
                onShortenSuccess(result.shortUrl, result.shortCode, result.originalUrl, result.expiresAt)
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                if (s.code == 401) {
                    app.authStorage.clearToken()
                    onSessionExpired()
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(logoutState) {
        if (logoutState is UiState.Success) onLogout()
    }

    if (showDatePicker) {
        ExpiryDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { millis ->
                expiresAtIso = formatIso8601(millis)
                expiresAtDisplay = formatDisplay(millis)
                showDatePicker = false
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("lil-url", color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToLinks) {
                        Icon(Icons.Filled.FormatListBulleted, contentDescription = "My links")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Log out")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {
            Spacer(Modifier.height(16.dp))

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        text = "Shorten a URL",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {

                    LilUrlTextField(
                        value = url,
                        onValueChange = { url = it; urlError = null },
                        label = "Original URL",
                        leadingIcon = Icons.Filled.Link,
                        errorMessage = urlError,
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next,
                    )

                    Spacer(Modifier.height(12.dp))

                    LilUrlTextField(
                        value = alias,
                        onValueChange = { alias = it; aliasError = null },
                        label = "Custom alias (optional)",
                        leadingIcon = Icons.Filled.Tag,
                        errorMessage = aliasError,
                        supportingText = if (aliasError == null) "4–32 chars: letters, numbers, - or _" else null,
                        imeAction = ImeAction.Next,
                    )

                    Spacer(Modifier.height(12.dp))

                    // Date field — Box overlay makes the whole field tappable
                    Box {
                        LilUrlTextField(
                            value = expiresAtDisplay,
                            onValueChange = {},
                            label = "Expiry date (optional)",
                            leadingIcon = Icons.Filled.CalendarToday,
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date")
                            },
                        )
                        // Transparent overlay captures taps anywhere on the field
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true },
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    LoadingButton(
                        text = "Shorten URL",
                        isLoading = shortenState is UiState.Loading,
                        onClick = {
                            var valid = true
                            if (!Validation.isValidUrl(url.trim())) {
                                urlError = "Enter a valid URL (e.g. https://example.com)"; valid = false
                            } else urlError = null
                            val a = alias.trim()
                            if (a.isNotEmpty() && !Validation.isValidAlias(a)) {
                                aliasError = "Alias: 4–32 chars, letters/numbers/-/_"; valid = false
                            } else aliasError = null
                            if (valid) viewModel.shorten(url.trim(), a.takeIf { it.isNotEmpty() }, expiresAtIso)
                        },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpiryDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
) {
    val state = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >= System.currentTimeMillis()
        },
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let(onDateSelected) ?: onDismiss() },
                enabled = state.selectedDateMillis != null,
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) {
        DatePicker(state = state)
    }
}

private fun formatIso8601(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

private fun formatDisplay(millis: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}
