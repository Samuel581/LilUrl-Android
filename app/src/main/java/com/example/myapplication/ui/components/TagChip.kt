package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.mock.tagColor
import com.example.myapplication.ui.theme.LilUrlTheme

@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier,
    color: Color = tagColor(tag),
    removable: Boolean = false,
    onRemove: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = color,
            )
            if (removable) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove $tag",
                    modifier = Modifier
                        .size(14.dp)
                        .clickable(onClick = onRemove),
                    tint = color.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagChipPreview(
    @PreviewParameter(TagChipParams::class) params: Triple<String, Color, Boolean>,
) {
    LilUrlTheme(darkTheme = false, dynamicColor = false) {
        TagChip(
            tag = params.first,
            color = if (params.second == Color.Unspecified) tagColor(params.first) else params.second,
            removable = params.third,
        )
    }
}
