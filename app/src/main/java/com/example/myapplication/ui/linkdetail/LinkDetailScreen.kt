package com.example.myapplication.ui.linkdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.myapplication.data.mock.MOCK_DAYS_LABELS
import com.example.myapplication.data.mock.MOCK_SPARKLINE_7D
import com.example.myapplication.data.mock.MockLink
import com.example.myapplication.ui.components.HealthBadge
import com.example.myapplication.ui.components.QrCodeDisplay
import com.example.myapplication.ui.components.SparklineChart
import com.example.myapplication.ui.components.StatCard
import com.example.myapplication.ui.components.TagChip
import com.example.myapplication.ui.components.WipBanner
import com.example.myapplication.ui.components.rememberQrBitmap
import com.example.myapplication.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinkDetailScreen(
    mainVm: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val link = mainVm.selectedLink

    if (link == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.Link, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                Text("Select a link to view details", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    val qrBitmap = rememberQrBitmap(link.shortUrl)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { DetailCard(link = link, qrBitmap = qrBitmap) }
            item {
                ActionButtons(
                    link = link,
                    qrBitmap = qrBitmap,
                    onEdit = { mainVm.openSettings() },
                    onMessage = { msg -> scope.launch { snackbar.showSnackbar(msg) } },
                )
            }

            item {
                WipBanner("Click stats, visitor counts and geographic data are not connected to the backend yet.")
            }
            item { StatsRow() }
            item { SparklineCard(onFullAnalytics = { mainVm.openAnalytics() }) }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailCard(link: MockLink, qrBitmap: ImageBitmap?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            QrCodeDisplay(bitmap = qrBitmap, size = 88.dp, modifier = Modifier.align(Alignment.TopEnd))

            Column(
                modifier = Modifier.fillMaxWidth().padding(end = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = link.shortUrl,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    HealthBadge(link.healthStatus)
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    link.tags.forEach { TagChip(tag = it) }
                    DomainChipSmall(link.domain)
                }

                Text(
                    text = link.originalUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetaItem(Icons.Rounded.CalendarMonth, link.createdAt)
                    if (link.expiresAt != null) MetaItem(Icons.Rounded.CalendarMonth, "Expires ${link.expiresAt}")
                }
            }
        }
    }
}

@Composable
private fun MetaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun ActionButtons(
    link: MockLink,
    qrBitmap: ImageBitmap?,
    onEdit: () -> Unit,
    onMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ActionButton(Icons.Rounded.ContentCopy, "Copy") {
            val mgr = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            mgr.setPrimaryClip(ClipData.newPlainText("short URL", link.shortUrl))
        }
        ActionButton(Icons.Rounded.Share, "Share") {
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, link.shortUrl)
            }, "Share link"))
        }
        ActionButton(Icons.Rounded.Download, "Save QR") {
            if (qrBitmap == null) { onMessage("QR not ready yet"); return@ActionButton }
            scope.launch {
                val saved = saveQrToGallery(context, qrBitmap, link.shortCode)
                onMessage(if (saved) "Saved to gallery" else "Failed to save QR")
            }
        }
        ActionButton(Icons.Rounded.QrCode, "Share QR") {
            if (qrBitmap == null) { onMessage("QR not ready yet"); return@ActionButton }
            scope.launch {
                shareQrImage(context, qrBitmap, link.shortCode)
            }
        }
        ActionButton(Icons.Rounded.Edit, "Edit") { onEdit() }
        ActionButton(
            Icons.Rounded.Delete, "Delete",
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.error,
        ) {}
    }
}

private fun ImageBitmap.toWhiteBackgroundBitmap(): Bitmap {
    val src = asAndroidBitmap()
    val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
    android.graphics.Canvas(out).apply {
        drawColor(android.graphics.Color.WHITE)
        drawBitmap(src, 0f, 0f, null)
    }
    return out
}

private suspend fun saveQrToGallery(context: Context, bitmap: ImageBitmap, shortCode: String): Boolean =
    withContext(Dispatchers.IO) {
        runCatching {
            val androidBitmap = bitmap.toWhiteBackgroundBitmap()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "qr_$shortCode.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/LilUrl")
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return@runCatching false
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            } else {
                val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "LilUrl")
                    .also { it.mkdirs() }
                File(dir, "qr_$shortCode.png").outputStream().use { out ->
                    androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
            true
        }.getOrDefault(false)
    }

private suspend fun shareQrImage(context: Context, bitmap: ImageBitmap, shortCode: String) =
    withContext(Dispatchers.IO) {
        runCatching {
            val cacheDir = File(context.cacheDir, "qr_codes").also { it.mkdirs() }
            val file = File(cacheDir, "qr_$shortCode.png")
            file.outputStream().use { out ->
                bitmap.toWhiteBackgroundBitmap().compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share QR Code"))
        }
    }

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSecondaryContainer,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        FilledTonalIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}

@Composable
private fun StatsRow() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Total clicks", "—", Icons.Rounded.TouchApp, Modifier.weight(1f))
            StatCard("Visitors", "—", Icons.Rounded.ShowChart, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Countries", "—", Icons.Rounded.Public, Modifier.weight(1f))
            StatCard("Mobile %", "—", Icons.Rounded.Link, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SparklineCard(onFullAnalytics: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Clicks — last 7 days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            WipBanner("Sample chart — will show real click data once the analytics backend is ready.")

            SparklineChart(data = MOCK_SPARKLINE_7D, modifier = Modifier.height(120.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                MOCK_DAYS_LABELS.forEach { day ->
                    Text(
                        day,
                        Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 10.sp,
                    )
                }
            }

            OutlinedButton(
                onClick = onFullAnalytics,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Full Analytics")
            }
        }
    }
}

@Composable
private fun DomainChipSmall(domain: String) {
    androidx.compose.material3.Surface(
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
