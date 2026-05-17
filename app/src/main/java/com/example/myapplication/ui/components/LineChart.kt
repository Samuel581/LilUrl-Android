package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LineChart(
    data: List<Float>,
    xLabels: List<String>,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val dotBg = MaterialTheme.colorScheme.surface

    Column(modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            if (data.size < 2) return@Canvas

            val max = data.max().coerceAtLeast(1f)
            val padY = size.height * 0.08f
            val chartH = size.height - padY * 2
            val stepX = size.width / (data.size - 1).toFloat()

            fun xAt(i: Int) = i * stepX
            fun yAt(v: Float) = padY + chartH - (v / max) * chartH * 0.9f

            // Grid lines
            repeat(5) { i ->
                val y = padY + chartH * i / 4f
                drawLine(gridColor, androidx.compose.ui.geometry.Offset(0f, y), androidx.compose.ui.geometry.Offset(size.width, y), strokeWidth = 0.5.dp.toPx())
            }

            // Curve
            val path = Path().apply {
                moveTo(xAt(0), yAt(data[0]))
                for (i in 1 until data.size) {
                    val x0 = xAt(i - 1); val y0 = yAt(data[i - 1])
                    val x1 = xAt(i); val y1 = yAt(data[i])
                    val cx = (x0 + x1) / 2f
                    cubicTo(cx, y0, cx, y1, x1, y1)
                }
            }
            drawPath(path, primary, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))

            // Dots every ~7 points to avoid clutter
            val step = (data.size / 7).coerceAtLeast(1)
            for (i in data.indices step step) {
                val x = xAt(i); val y = yAt(data[i])
                drawCircle(dotBg, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y))
                drawCircle(primary, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, y), style = Stroke(1.5.dp.toPx()))
            }
        }

        // X-axis labels
        if (xLabels.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                xLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}
