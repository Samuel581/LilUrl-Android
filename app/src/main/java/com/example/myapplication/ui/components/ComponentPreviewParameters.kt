package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Link
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SparklineChartData : PreviewParameterProvider<List<Float>> {
    override val values = sequenceOf(
        emptyList(),
        listOf(10f, 25f, 15f, 40f, 30f),
        listOf(10f, 25f, 15f, 40f, 30f, 55f, 35f, 60f, 45f, 70f, 50f, 80f, 65f, 90f, 75f, 85f, 95f, 70f, 88f, 78f, 92f, 82f, 96f, 86f, 100f, 90f, 94f, 84f, 98f, 88f),
    )
}

class LineChartData : PreviewParameterProvider<Pair<List<Float>, List<String>>> {
    override val values = sequenceOf(
        listOf(12f, 19f, 14f, 25f, 22f, 30f, 28f) to listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        listOf(5f, 10f, 8f, 15f, 12f, 20f, 18f, 25f, 22f, 30f, 28f, 35f, 32f, 40f, 38f, 45f, 42f, 50f, 48f, 55f, 52f, 60f, 58f, 65f, 62f, 70f, 68f, 75f, 72f, 80f) to (1..30).map { "$it" },
    )
}

class BarChartEntries : PreviewParameterProvider<List<Pair<String, Float>>> {
    override val values = sequenceOf(
        listOf("Desktop" to 0.65f, "Mobile" to 0.25f, "Tablet" to 0.10f),
        listOf("US" to 0.42f, "UK" to 0.18f, "DE" to 0.12f, "FR" to 0.09f, "JP" to 0.08f, "Other" to 0.11f),
    )
}

class HeatmapGridData : PreviewParameterProvider<List<List<Float>>> {
    override val values = sequenceOf(
        listOf(
            listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f, 0.6f, 0.4f, 0.2f, 0.1f, 0.0f, 0.0f, 0.1f, 0.3f, 0.5f, 0.8f, 0.9f, 0.7f, 0.5f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.1f),
            listOf(0.0f, 0.1f, 0.2f, 0.4f, 0.6f, 0.8f, 0.5f, 0.3f, 0.1f, 0.0f, 0.0f, 0.0f, 0.2f, 0.4f, 0.7f, 0.9f, 0.6f, 0.4f, 0.2f, 0.1f, 0.0f, 0.0f, 0.0f, 0.1f),
            listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f, 0.7f, 0.5f, 0.3f, 0.1f, 0.0f, 0.0f, 0.1f, 0.3f, 0.5f, 0.8f, 1.0f, 0.8f, 0.6f, 0.4f, 0.2f, 0.1f, 0.0f, 0.0f, 0.2f),
        ),
        listOf(
            listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.1f, 0.2f, 0.3f),
            listOf(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.0f, 0.1f, 0.2f, 0.3f),
            listOf(0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.1f, 0.2f, 0.3f, 0.4f),
            listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.1f, 0.2f, 0.3f),
            listOf(0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f),
            listOf(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.2f),
            listOf(0.0f, 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f, 0.2f),
        ),
    )
}

class QrBitmapState : PreviewParameterProvider<String?> {
    override val values = sequenceOf(
        null,
        "https://lil-url.com/s/abc123",
    )
}

class HealthStatus : PreviewParameterProvider<Int> {
    override val values = sequenceOf(-1, 200, 404, 500)
}

class WipLabels : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Analytics coming soon",
        "Settings under construction",
    )
}

class StatCardParams : PreviewParameterProvider<Triple<String, String, androidx.compose.ui.graphics.vector.ImageVector>> {
    override val values = sequenceOf(
        Triple("Total Clicks", "1,234", Icons.Filled.Favorite),
        Triple("Unique Visitors", "567", Icons.Filled.Analytics),
        Triple("Active Links", "42", Icons.Filled.Link),
    )
}

class TagChipParams : PreviewParameterProvider<Triple<String, Color, Boolean>> {
    override val values = sequenceOf(
        Triple("marketing", Color.Unspecified, false),
        Triple("promo", Color(0xFF6750A4), false),
        Triple("removable-tag", Color.Unspecified, true),
    )
}

class TextFieldParams : PreviewParameterProvider<TextFieldPreviewConfig> {
    override val values = sequenceOf(
        TextFieldPreviewConfig("Enter URL", "https://example.com", null, false),
        TextFieldPreviewConfig("Enter URL", "", "Invalid URL format", false),
        TextFieldPreviewConfig("Custom slug", "my-link", null, false),
        TextFieldPreviewConfig("Disabled field", "", null, true),
    )
}

data class TextFieldPreviewConfig(
    val label: String,
    val value: String,
    val error: String?,
    val enabled: Boolean,
)

class PasswordFieldParams : PreviewParameterProvider<PasswordFieldPreviewConfig> {
    override val values = sequenceOf(
        PasswordFieldPreviewConfig("Password", "", null, true),
        PasswordFieldPreviewConfig("Password", "secret123", null, true),
        PasswordFieldPreviewConfig("Password", "", "Password must be at least 8 characters", true),
        PasswordFieldPreviewConfig("Password", "", null, false),
    )
}

data class PasswordFieldPreviewConfig(
    val label: String,
    val value: String,
    val error: String?,
    val enabled: Boolean,
)

class LoadingButtonParams : PreviewParameterProvider<Triple<String, Boolean, Boolean>> {
    override val values = sequenceOf(
        Triple("Sign In", false, true),
        Triple("Sign In", true, true),
        Triple("Sign In", false, false),
    )
}
