package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.LilUrlTheme

@Composable
fun SparklineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxWidth()) {
        if (data.size < 2) return@Canvas

        val max = data.max()
        val min = data.min()
        val range = (max - min).takeIf { it > 0f } ?: 1f
        val padY = size.height * 0.08f
        val chartH = size.height - padY * 2

        val stepX = size.width / (data.size - 1).toFloat()
        fun xAt(i: Int) = i * stepX
        fun yAt(v: Float) = padY + chartH - (v - min) / range * chartH

        // Filled area under curve
        val fillPath = Path().apply {
            moveTo(xAt(0), size.height)
            lineTo(xAt(0), yAt(data[0]))
            for (i in 1 until data.size) {
                val x0 = xAt(i - 1); val y0 = yAt(data[i - 1])
                val x1 = xAt(i); val y1 = yAt(data[i])
                val cx = (x0 + x1) / 2f
                cubicTo(cx, y0, cx, y1, x1, y1)
            }
            lineTo(xAt(data.size - 1), size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primary.copy(alpha = 0.22f), primary.copy(alpha = 0f)),
                startY = padY,
                endY = size.height,
            ),
        )

        // Stroke line
        val strokePath = Path().apply {
            moveTo(xAt(0), yAt(data[0]))
            for (i in 1 until data.size) {
                val x0 = xAt(i - 1); val y0 = yAt(data[i - 1])
                val x1 = xAt(i); val y1 = yAt(data[i])
                val cx = (x0 + x1) / 2f
                cubicTo(cx, y0, cx, y1, x1, y1)
            }
        }
        drawPath(
            path = strokePath,
            color = primary,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SparklineChartPreview(
    @PreviewParameter(SparklineChartData::class) data: List<Float>,
) {
    LilUrlTheme(darkTheme = false, dynamicColor = false) {
        SparklineChart(data, Modifier.height(64.dp))
    }
}
