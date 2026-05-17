package com.example.myapplication.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.LilUrlTheme
import com.example.myapplication.ui.theme.WipContainer
import com.example.myapplication.ui.theme.WipContainerDark
import com.example.myapplication.ui.theme.WipOnContainer
import com.example.myapplication.ui.theme.WipOnContainerDark

@Composable
fun WipBanner(
    label: String,
    modifier: Modifier = Modifier,
) {
    val dark = isSystemInDarkTheme()
    val bg = if (dark) WipContainerDark else WipContainer
    val fg = if (dark) WipOnContainerDark else WipOnContainer

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = bg,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Construction,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = fg,
            )
            Text(
                text = label,
                color = fg,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WipBannerPreview(
    @PreviewParameter(WipLabels::class) label: String,
) {
    LilUrlTheme(darkTheme = false, dynamicColor = false) {
        WipBanner(label)
    }
}
