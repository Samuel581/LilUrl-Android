package com.example.myapplication.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.HealthGreenContainer
import com.example.myapplication.ui.theme.HealthGreenContainerDark
import com.example.myapplication.ui.theme.HealthGreenOnContainer
import com.example.myapplication.ui.theme.HealthGreenOnContainerDark
import com.example.myapplication.ui.theme.HealthRedContainer
import com.example.myapplication.ui.theme.HealthRedContainerDark
import com.example.myapplication.ui.theme.HealthRedOnContainer
import com.example.myapplication.ui.theme.HealthRedOnContainerDark
import com.example.myapplication.ui.theme.LilUrlTheme

/** status = -1 means unknown (not yet checked by backend). Renders a neutral "—" badge. */
@Composable
fun HealthBadge(status: Int, modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()

    val (bg, fg, label) = when {
        status == -1 -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "—",
        )
        status == 200 -> Triple(
            if (dark) HealthGreenContainerDark else HealthGreenContainer,
            if (dark) HealthGreenOnContainerDark else HealthGreenOnContainer,
            "200 OK",
        )
        else -> Triple(
            if (dark) HealthRedContainerDark else HealthRedContainer,
            if (dark) HealthRedOnContainerDark else HealthRedOnContainer,
            "$status",
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = bg,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HealthBadgePreview(
    @PreviewParameter(HealthStatus::class) status: Int,
) {
    LilUrlTheme(darkTheme = false, dynamicColor = false) {
        HealthBadge(status)
    }
}
