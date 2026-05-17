package com.example.myapplication.ui.linksettings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.mock.MOCK_ALL_TAGS
import com.example.myapplication.data.mock.tagColor
import com.example.myapplication.ui.components.HealthBadge
import com.example.myapplication.ui.components.TagChip
import com.example.myapplication.ui.components.WipBanner
import com.example.myapplication.ui.main.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinkSettingsScreen(
    mainVm: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val link = mainVm.selectedLink

    if (link == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.Settings, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                Text("Select a link to edit settings", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    // Local editable state
    var tags by remember(link.id) { mutableStateOf(link.tags.toMutableList().also {}) }
    var tagsList by remember(link.id) { mutableStateOf(link.tags) }
    var tagInput by remember { mutableStateOf("") }
    var passwordEnabled by remember(link.id) { mutableStateOf(link.isPasswordProtected) }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var ogTitle by remember(link.id) { mutableStateOf(link.shortCode.replaceFirstChar { it.uppercase() }) }
    var ogDesc by remember(link.id) { mutableStateOf("Shortened link: ${link.originalUrl}") }
    var ogImageUrl by remember { mutableStateOf("") }
    var notifyOnDead by remember { mutableStateOf(false) }
    var isRechecking by remember { mutableStateOf(false) }

    val previewTitle by remember { derivedStateOf { ogTitle.ifEmpty { "No title" } } }
    val previewDesc by remember { derivedStateOf { ogDesc.ifEmpty { "No description" } } }

    val suggestions = remember(tagsList) { MOCK_ALL_TAGS.filter { it !in tagsList } }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ─── Tags ─────────────────────────────────────────────
            item {
                SettingsCard(title = "Tags & labels") {
                    WipBanner("Tag support is not yet connected to the backend — changes won't be saved.")
                    Spacer(Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        tagsList.forEach { tag ->
                            TagChip(tag = tag, removable = true, onRemove = { tagsList = tagsList - tag })
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = tagInput,
                            onValueChange = { tagInput = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("New tag") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                        )
                        if (tagInput.isNotEmpty()) {
                            TextButton(onClick = {
                                val t = tagInput.trim()
                                if (t.isNotEmpty() && t !in tagsList) tagsList = tagsList + t
                                tagInput = ""
                            }) { Text("Add") }
                        }
                    }
                    if (suggestions.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Suggestions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            suggestions.forEach { s ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.clickable { tagsList = tagsList + s },
                                ) {
                                    Text(s, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // ─── Security ─────────────────────────────────────────
            item {
                SettingsCard(title = "Security") {
                    WipBanner("Password protection is not yet supported by the backend.")
                    Spacer(Modifier.height(12.dp))
                    ListItem(
                        headlineContent = { Text("Password protection") },
                        supportingContent = { Text("Visitors must enter a password before redirect", style = MaterialTheme.typography.bodySmall) },
                        trailingContent = { Switch(checked = passwordEnabled, onCheckedChange = { passwordEnabled = it }) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.padding(horizontal = 0.dp),
                    )
                    AnimatedVisibility(
                        visible = passwordEnabled,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
                }
            }

            // ─── OG Preview ───────────────────────────────────────
            item {
                SettingsCard(title = "Link preview (OG)") {
                    WipBanner("OG metadata customization is not yet supported by the backend.")
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(modifier = Modifier.weight(0.55f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = ogTitle,
                                onValueChange = { ogTitle = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("OG Title") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                            )
                            OutlinedTextField(
                                value = ogDesc,
                                onValueChange = { ogDesc = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("OG Description") },
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp),
                            )
                            OutlinedTextField(
                                value = ogImageUrl,
                                onValueChange = { ogImageUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("OG Image URL") },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Rounded.Upload, contentDescription = "Upload image", tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                        // Live preview
                        Card(
                            modifier = Modifier.weight(0.45f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                            elevation = CardDefaults.cardElevation(0.dp),
                        ) {
                            Column {
                                // Image placeholder
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .background(striped()),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "[ image ]",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    )
                                }
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(link.domain, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontSize = 9.sp)
                                    Text(previewTitle, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                                    Text(previewDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // ─── Link health ──────────────────────────────────────
            item {
                SettingsCard(title = "Link health") {
                    WipBanner("Health checks and dead-link notifications are not yet connected to the backend.")
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HealthBadge(link.healthStatus)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Last checked 3 min ago", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(
                            onClick = { isRechecking = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Re-check", fontSize = 12.sp)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                    ListItem(
                        headlineContent = { Text("Notify me if link goes dead") },
                        supportingContent = { Text("Get an email alert when the destination returns 4xx or 5xx", style = MaterialTheme.typography.bodySmall) },
                        trailingContent = { Switch(checked = notifyOnDead, onCheckedChange = { notifyOnDead = it }) },
                        leadingContent = { Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    )
                }
            }
        }

        // ─── Bottom bar ───────────────────────────────────────────
        Surface(
            tonalElevation = 2.dp,
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = {}) { Text("Cancel") }
                Button(onClick = {}, shape = RoundedCornerShape(12.dp)) { Text("Save changes") }
            }
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 14.dp))
            content()
        }
    }
}

@Composable
private fun striped(): androidx.compose.ui.graphics.Brush {
    val color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    return androidx.compose.ui.graphics.Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color.Transparent,
            0.5f to Color.Transparent,
            0.5f to color,
            1.0f to color,
        ),
    )
}
