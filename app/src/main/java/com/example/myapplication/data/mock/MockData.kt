package com.example.myapplication.data.mock

import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.TagAmber
import com.example.myapplication.ui.theme.TagEmerald
import com.example.myapplication.ui.theme.TagRose
import com.example.myapplication.ui.theme.TagSky
import com.example.myapplication.ui.theme.TagTeal
import com.example.myapplication.ui.theme.TagViolet
import kotlin.math.abs
import kotlin.math.sin

data class MockLink(
    val id: String,
    val shortUrl: String,
    val shortCode: String,
    val originalUrl: String,
    val domain: String,
    val tags: List<String>,
    val isPasswordProtected: Boolean,
    val healthStatus: Int,
    val totalClicks: Int,
    val createdAt: String,
    val expiresAt: String? = null,
)

data class GeoEntry(val flag: String, val name: String, val percent: Float)

val TAG_PALETTE: List<Color> = listOf(TagViolet, TagTeal, TagAmber, TagRose, TagSky, TagEmerald)

fun tagColor(tag: String): Color = TAG_PALETTE[abs(tag.hashCode()) % TAG_PALETTE.size]

val MOCK_ALL_TAGS = listOf("portfolio", "work", "social", "tools", "docs", "personal")

val MOCK_LINKS = listOf(
    MockLink("1", "lil.url/portfolio", "portfolio", "https://github.com/samuel-ortiz/portfolio", "github.com", listOf("portfolio", "work"), false, 200, 1482, "May 1, 2026"),
    MockLink("2", "lil.url/resume", "resume", "https://drive.google.com/file/d/resume.pdf", "drive.google.com", listOf("work", "docs"), false, 200, 324, "Apr 28, 2026", "Jun 1, 2026"),
    MockLink("3", "lil.url/x", "x", "https://twitter.com/samuelortiz", "twitter.com", listOf("social"), false, 200, 89, "Apr 20, 2026"),
    MockLink("4", "lil.url/notion", "notion", "https://notion.so/my-workspace", "notion.so", listOf("tools"), true, 200, 55, "Apr 15, 2026"),
    MockLink("5", "lil.url/figma-ds", "figma-ds", "https://figma.com/file/design-system-2025", "figma.com", listOf("work", "tools"), false, 404, 210, "Mar 10, 2026"),
    MockLink("6", "lil.url/blog", "blog", "https://samuelortiz.dev/blog", "samuelortiz.dev", listOf("personal", "portfolio"), false, 200, 762, "Feb 5, 2026"),
)

// Click data arrays for different time ranges
val MOCK_SPARKLINE_7D = listOf(42f, 88f, 61f, 130f, 95f, 54f, 22f)
val MOCK_DAYS_LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private fun waveData(points: Int, amplitude: Float, offset: Float, noise: Float): List<Float> =
    (0 until points).map { i ->
        val wave = (sin(i * 0.3) * amplitude + offset).toFloat()
        val n = ((i * 17 + 7) % 20 - 10) * noise
        (wave + n).coerceAtLeast(5f)
    }

val MOCK_LINE_7D: List<Float> = MOCK_SPARKLINE_7D
val MOCK_LINE_7D_LABELS: List<String> = MOCK_DAYS_LABELS
val MOCK_LINE_30D: List<Float> = waveData(30, 60f, 120f, 0.5f)
val MOCK_LINE_30D_LABELS: List<String> = (1..30 step 5).map { "May $it" }
val MOCK_LINE_90D: List<Float> = waveData(90, 80f, 160f, 0.6f)
val MOCK_LINE_90D_LABELS: List<String> = listOf("Mar", "Apr", "May")
val MOCK_LINE_ALL: List<Float> = waveData(120, 100f, 200f, 0.7f)
val MOCK_LINE_ALL_LABELS: List<String> = listOf("Feb", "Mar", "Apr", "May")

// Stats per time range: [clicks, visitors, countries, mobilePct]
val MOCK_STATS_7D = listOf("492", "341", "18", "64%")
val MOCK_STATS_30D = listOf("2.1K", "1.4K", "31", "61%")
val MOCK_STATS_90D = listOf("6.8K", "4.2K", "47", "59%")
val MOCK_STATS_ALL = listOf("14.2K", "9.1K", "52", "61%")

val MOCK_GEO_COUNTRIES = listOf(
    GeoEntry("🇺🇸", "United States", 0.42f),
    GeoEntry("🇬🇧", "United Kingdom", 0.18f),
    GeoEntry("🇩🇪", "Germany", 0.09f),
    GeoEntry("🇨🇦", "Canada", 0.07f),
    GeoEntry("🇫🇷", "France", 0.06f),
    GeoEntry("🇦🇺", "Australia", 0.05f),
    GeoEntry("🌍", "Other", 0.13f),
)

val MOCK_GEO_CITIES = listOf(
    GeoEntry("🏙️", "New York", 0.22f),
    GeoEntry("🌉", "San Francisco", 0.15f),
    GeoEntry("🏰", "London", 0.12f),
    GeoEntry("🗼", "Berlin", 0.08f),
    GeoEntry("🍁", "Toronto", 0.07f),
    GeoEntry("🌅", "Los Angeles", 0.06f),
    GeoEntry("🌍", "Other", 0.30f),
)

val MOCK_DEVICE = listOf(
    "Mobile" to 0.62f,
    "Desktop" to 0.31f,
    "Tablet" to 0.07f,
)

val MOCK_BROWSER = listOf(
    "Chrome" to 0.51f,
    "Safari" to 0.28f,
    "Firefox" to 0.12f,
    "Other" to 0.09f,
)

// 7 rows (Mon=0 .. Sun=6) × 24 cols (hours). Deterministic "random" intensity.
val MOCK_HEATMAP: List<List<Float>> = (0 until 7).map { day ->
    (0 until 24).map { hour ->
        val isWeekday = day < 5
        val isBusinessHour = hour in 9..18
        val base = when {
            isWeekday && isBusinessHour -> 0.5f + (day * 7 + hour) % 5 * 0.09f
            isWeekday -> 0.1f + (day * 3 + hour) % 3 * 0.08f
            isBusinessHour -> 0.2f + hour % 4 * 0.07f
            else -> 0.05f + hour % 3 * 0.04f
        }
        base.coerceIn(0f, 1f)
    }
}
