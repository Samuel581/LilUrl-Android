package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun QrCodeDisplay(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val matrix: BitMatrix? by produceState<BitMatrix?>(initialValue = null, key1 = url) {
        value = withContext(Dispatchers.Default) {
            runCatching {
                QRCodeWriter().encode(
                    url,
                    BarcodeFormat.QR_CODE,
                    512,
                    512,
                    mapOf(EncodeHintType.MARGIN to 0),
                )
            }.getOrNull()
        }
    }

    val cellColor = MaterialTheme.colorScheme.onSurface
    val bgColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(4.dp),
    ) {
        val m = matrix ?: return@Canvas
        val cellW = this.size.width / m.width
        val cellH = this.size.height / m.height
        for (y in 0 until m.height) {
            for (x in 0 until m.width) {
                if (m[x, y]) {
                    drawRect(
                        color = cellColor,
                        topLeft = Offset(x * cellW, y * cellH),
                        size = Size(cellW, cellH),
                    )
                }
            }
        }
    }
}
