package com.example.myapplication.ui.analytics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.mock.GeoEntry
import com.example.myapplication.data.mock.MOCK_BROWSER
import com.example.myapplication.data.mock.MOCK_DEVICE
import com.example.myapplication.data.mock.MOCK_GEO_CITIES
import com.example.myapplication.data.mock.MOCK_GEO_COUNTRIES
import com.example.myapplication.data.mock.MOCK_HEATMAP
import com.example.myapplication.data.mock.MOCK_LINE_30D
import com.example.myapplication.data.mock.MOCK_LINE_30D_LABELS
import com.example.myapplication.data.mock.MOCK_LINE_7D
import com.example.myapplication.data.mock.MOCK_LINE_7D_LABELS
import com.example.myapplication.data.mock.MOCK_LINE_90D
import com.example.myapplication.data.mock.MOCK_LINE_90D_LABELS
import com.example.myapplication.data.mock.MOCK_LINE_ALL
import com.example.myapplication.data.mock.MOCK_LINE_ALL_LABELS
import com.example.myapplication.data.mock.MOCK_STATS_30D
import com.example.myapplication.data.mock.MOCK_STATS_7D
import com.example.myapplication.data.mock.MOCK_STATS_90D
import com.example.myapplication.data.mock.MOCK_STATS_ALL
import com.example.myapplication.ui.components.BarChart
import com.example.myapplication.ui.components.HeatmapGrid
import com.example.myapplication.ui.components.LineChart
import com.example.myapplication.ui.components.StatCard
import com.example.myapplication.ui.components.WipBanner
import com.example.myapplication.ui.main.MainViewModel

private val TIME_RANGES = listOf("7d", "30d", "90d", "All")
private val GRANULARITIES = listOf("Daily", "Weekly", "Monthly")
private val GEO_VIEWS = listOf("Country", "City")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    mainVm: MainViewModel,
    modifier: Modifier = Modifier,
) {
    var timeRange by remember { mutableStateOf(0) }
    var granularity by remember { mutableStateOf(0) }
    var geoView by remember { mutableStateOf(0) }

    val link = mainVm.selectedLink

    if (link == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Rounded.BarChart, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                Text("Select a link to view analytics", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    val stats = when (timeRange) { 0 -> MOCK_STATS_7D; 1 -> MOCK_STATS_30D; 2 -> MOCK_STATS_90D; else -> MOCK_STATS_ALL }
    val lineData = when (timeRange) { 0 -> MOCK_LINE_7D; 1 -> MOCK_LINE_30D; 2 -> MOCK_LINE_90D; else -> MOCK_LINE_ALL }
    val lineLabels = when (timeRange) { 0 -> MOCK_LINE_7D_LABELS; 1 -> MOCK_LINE_30D_LABELS; 2 -> MOCK_LINE_90D_LABELS; else -> MOCK_LINE_ALL_LABELS }
    val geoEntries = if (geoView == 0) MOCK_GEO_COUNTRIES else MOCK_GEO_CITIES

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // Top-level WIP notice
        item {
            WipBanner("This entire screen uses sample data. Analytics will be live once the backend endpoint is connected.")
        }

        // Header: short URL + time range
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(link.shortUrl, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                SingleChoiceSegmentedButtonRow {
                    TIME_RANGES.forEachIndexed { i, label ->
                        SegmentedButton(
                            selected = timeRange == i,
                            onClick = { timeRange = i },
                            shape = SegmentedButtonDefaults.itemShape(i, TIME_RANGES.size),
                            label = { Text(label, fontSize = 11.sp) },
                        )
                    }
                }
            }
        }

        // Four stat cards
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Clicks", stats[0], Icons.Rounded.TouchApp, Modifier.weight(1f))
                StatCard("Visitors", stats[1], Icons.Rounded.PhoneAndroid, Modifier.weight(1f))
                StatCard("Countries", stats[2], Icons.Rounded.Public, Modifier.weight(1f))
                StatCard("Peak time", stats.getOrElse(3) { "—" }, Icons.Rounded.Schedule, Modifier.weight(1f))
            }
        }

        // Line chart
        item {
            SectionCard(title = "Clicks over time") {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    GRANULARITIES.forEachIndexed { i, label ->
                        SegmentedButton(
                            selected = granularity == i,
                            onClick = { granularity = i },
                            shape = SegmentedButtonDefaults.itemShape(i, GRANULARITIES.size),
                            label = { Text(label, fontSize = 11.sp) },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                LineChart(data = lineData, xLabels = lineLabels)
            }
        }

        // Geo + Device side by side
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Geographic
                SectionCard(modifier = Modifier.weight(1f), title = "Geographic") {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        GEO_VIEWS.forEachIndexed { i, label ->
                            SegmentedButton(
                                selected = geoView == i,
                                onClick = { geoView = i },
                                shape = SegmentedButtonDefaults.itemShape(i, GEO_VIEWS.size),
                                label = { Text(label, fontSize = 11.sp) },
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    GeoList(entries = geoEntries)
                }
                // Device + Browser stacked
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionCard(title = "Devices") {
                        BarChart(
                            entries = MOCK_DEVICE,
                            barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        )
                    }
                    SectionCard(title = "Browsers") {
                        BarChart(
                            entries = MOCK_BROWSER,
                            barColor = Color(0xFF0D9488).copy(alpha = 0.85f),
                        )
                    }
                }
            }
        }

        // Heatmap
        item {
            SectionCard(title = "Peak hours") {
                HeatmapGrid(data = MOCK_HEATMAP, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun GeoList(entries: List<GeoEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEach { (flag, name, percent) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(flag, fontSize = 14.sp)
                Text(name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), maxLines = 1)
                Text("${(percent * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}
