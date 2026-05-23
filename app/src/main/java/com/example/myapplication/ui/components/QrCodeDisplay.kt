package com.example.myapplication.ui.components

import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.SmolifyTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberQrBitmap(url: String): ImageBitmap? {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = url) {
        value = withContext(Dispatchers.Default) {
            val matrix = runCatching {
                QRCodeWriter().encode(
                    url,
                    BarcodeFormat.QR_CODE,
                    512,
                    512,
                    mapOf(EncodeHintType.MARGIN to 0),
                )
            }.getOrNull() ?: return@withContext null

            val pixels = IntArray(matrix.width * matrix.height) { i ->
                if (matrix[i % matrix.width, i / matrix.width]) android.graphics.Color.BLACK else 0
            }
            Bitmap.createBitmap(pixels, matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
                .asImageBitmap()
        }
    }
    return bitmap
}

@Composable
fun QrCodeDisplay(
    bitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val bgColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(4.dp),
    ) {
        val bmp = bitmap ?: return@Canvas
        drawImage(
            image = bmp,
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(this.size.width.toInt(), this.size.height.toInt()),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QrCodeDisplayPreview() {
    SmolifyTheme(darkTheme = false, dynamicColor = false) {
        val bitmap = rememberQrBitmap("https://lil-url-production.up.railway.app/s/example")
        QrCodeDisplay(bitmap)
    }
}
