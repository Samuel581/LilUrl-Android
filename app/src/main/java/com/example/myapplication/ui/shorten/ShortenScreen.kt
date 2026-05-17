package com.example.myapplication.ui.shorten

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.LilUrlApp
import com.example.myapplication.data.mock.MockLink
import com.example.myapplication.data.mock.tagColor
import com.example.myapplication.ui.components.HealthBadge
import com.example.myapplication.ui.components.LoadingButton
import com.example.myapplication.ui.components.TagChip
import com.example.myapplication.ui.main.MainViewModel
import com.example.myapplication.util.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShortenScreen(
    mainVm: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as LilUrlApp
    val vm: ShortenViewModel = viewModel(factory = ShortenViewModel.factory(app.urlRepository))
    val snackbar = remember { SnackbarHostState() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // Shorten result feedback
    LaunchedEffect(vm.shortenState) {
        when (val s = vm.shortenState) {
            is UiState.Success -> { snackbar.showSnackbar("Shortened: ${s.data}"); vm.resetShortenState() }
            is UiState.Error -> { snackbar.showSnackbar(s.message); vm.resetShortenState() }
            else -> Unit
        }
    }

    if (showDatePicker) {
        ExpiryDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { millis ->
                vm.expiryIso = formatIso8601(millis)
                vm.expiryDisplay = formatDisplay(millis)
                showDatePicker = false
            },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ─── Shorten form card ────────────────────────────────
            item {
                SectionCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                        OutlinedTextField(
                            value = vm.longUrl,
                            onValueChange = { vm.longUrl = it; vm.urlError = null },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Long URL") },
                            isError = vm.urlError != null,
                            supportingText = vm.urlError?.let { err -> { Text(err) } },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                        )

                        OutlinedTextField(
                            value = vm.slug,
                            onValueChange = { vm.slug = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Custom slug") },
                            prefix = {
                                Text("lil.url/", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                        )

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (vm.expiryDisplay.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            ),
                        ) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(vm.expiryDisplay.ifEmpty { "No expiry date" })
                        }

                        // Tags
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Tags", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (vm.tags.isNotEmpty()) {
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    vm.tags.forEach { tag ->
                                        TagChip(tag = tag, removable = true, onRemove = { vm.removeTag(tag) })
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = vm.tagInput,
                                    onValueChange = { vm.tagInput = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("New tag") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                if (vm.tagInput.isNotEmpty()) {
                                    TextButton(onClick = { vm.addTag(vm.tagInput) }) { Text("Add") }
                                }
                            }
                        }

                        // Password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("Password protect", style = MaterialTheme.typography.bodyMedium)
                                Text("Visitors must enter a password", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = vm.passwordEnabled, onCheckedChange = { vm.passwordEnabled = it })
                        }
                        AnimatedVisibility(visible = vm.passwordEnabled) {
                            OutlinedTextField(
                                value = vm.password,
                                onValueChange = { vm.password = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Password") },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    TextButton(onClick = { showPassword = !showPassword }) {
                                        Text(if (showPassword) "Hide" else "Show", style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                            )
                        }

                        HorizontalDivider(thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextButton(onClick = { mainVm.openSettings() }) { Text("Customize preview") }
                            LoadingButton(
                                text = "Shorten",
                                isLoading = vm.shortenState is UiState.Loading,
                                onClick = { vm.shorten() },
                            )
                        }
                    }
                }
            }

            // ─── Links list header ────────────────────────────────
            item {
                LinksListHeader(
                    linksState = vm.linksState,
                    onRefresh = { vm.loadLinks() },
                )
            }

            // ─── Links list body ──────────────────────────────────
            when (val state = vm.linksState) {
                is UiState.Loading -> item {
                    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                is UiState.Error -> item {
                    ErrorState(message = state.message, onRetry = { vm.loadLinks() })
                }
                is UiState.Success -> {
                    val links = vm.filteredLinks
                    if (links.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                                Text("No links yet. Shorten your first URL above.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(links, key = { it.id }) { link ->
                            LinkRow(
                                link = link,
                                isActive = link.id == mainVm.selectedLink?.id,
                                onClick = { mainVm.selectLink(link) },
                            )
                        }
                    }
                }
                else -> Unit
            }
        }

        // Snackbar anchored to bottom
        SnackbarHost(snackbar, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
    }
}

@Composable
private fun LinksListHeader(
    linksState: UiState<List<MockLink>>,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Rounded.Link, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = when (linksState) {
                    is UiState.Success -> "Your links (${linksState.data.size})"
                    is UiState.Loading -> "Your links"
                    else -> "Your links"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = onRetry, shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Retry")
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box(Modifier.padding(16.dp)) { content() }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinkRow(
    link: MockLink,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (isActive) drawRect(color = primary, topLeft = Offset.Zero, size = Size(3.dp.toPx(), size.height))
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            if (isActive) 1.dp else 0.5.dp,
            if (isActive) primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
        ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://www.google.com/s2/favicons?domain=${link.domain}&sz=32")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = link.shortUrl,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    // Only show lock icon when password status is actually known
                    if (link.isPasswordProtected) {
                        Icon(Icons.Rounded.Lock, "Protected", Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DomainChip(link.domain)
                    link.tags.forEach { TagChip(tag = it) }
                    // health = -1 means not checked yet; HealthBadge renders "—" for that
                    HealthBadge(link.healthStatus)
                }
                Text(
                    text = link.originalUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                )
                if (link.createdAt != "—") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Created ${link.createdAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        if (link.expiresAt != null) Text("Expires ${link.expiresAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    }
                }
            }

            IconButton(onClick = onClick) {
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, "View details", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DomainChip(domain: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = domain,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpiryDatePickerDialog(onDismiss: () -> Unit, onDateSelected: (Long) -> Unit) {
    val state = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= System.currentTimeMillis()
        },
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { state.selectedDateMillis?.let(onDateSelected) ?: onDismiss() }, enabled = state.selectedDateMillis != null) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) { DatePicker(state = state) }
}

private fun formatIso8601(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(millis))

private fun formatDisplay(millis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(millis))
