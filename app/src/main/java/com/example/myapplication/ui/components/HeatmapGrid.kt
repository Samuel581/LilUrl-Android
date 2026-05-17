package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.LilUrlTheme

private val DAY_LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val HOUR_TICKS = mapOf(0 to "12am", 6 to "6am", 12 to "12pm", 18 to "6pm", 23 to "12am")

@Composable
fun HeatmapGrid(
    data: List<List<Float>>,
    modifier: Modifier = Modifier,
) {
    val low = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val high = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        data.forEachIndexed { dayIdx, hours ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = DAY_LABELS.getOrElse(dayIdx) { "" },
                    modifier = Modifier.width(28.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    fontSize = 10.sp,
                )
                hours.forEach { intensity ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(lerp(low, high, intensity.coerceIn(0f, 1f))),
                    )
                }
            }
            if (dayIdx < data.size - 1) Spacer(Modifier.height(2.dp))
        }

        // Hour tick labels
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(30.dp))
            Box(Modifier.weight(1f)) {
                HOUR_TICKS.forEach { (hour, label) ->
                    val fraction = hour / 23f
                    Text(
                        text = label,
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .align(Alignment.TopEnd),
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                        fontSize = 9.sp,
                    )
                }
            }
        }

        // Legend
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Low", style = MaterialTheme.typography.labelSmall, color = labelColor, fontSize = 10.sp)
            Row(Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))) {
                // Gradient legend bar via 10 steps
                repeat(10) { i ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(lerp(low, high, i / 9f)),
                    )
                }
            }
            Text("High", style = MaterialTheme.typography.labelSmall, color = labelColor, fontSize = 10.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeatmapGridPreview(
    @PreviewParameter(HeatmapGridData::class) data: List<List<Float>>,
) {
    LilUrlTheme(darkTheme = false, dynamicColor = false) {
        HeatmapGrid(data, Modifier.fillMaxWidth())
    }
}
