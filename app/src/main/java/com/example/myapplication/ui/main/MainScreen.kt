package com.example.myapplication.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.analytics.AnalyticsScreen
import com.example.myapplication.ui.linkdetail.LinkDetailScreen
import com.example.myapplication.ui.linksettings.LinkSettingsScreen
import com.example.myapplication.ui.shorten.ShortenScreen

private data class TabItem(val icon: ImageVector, val label: String)

private val TABS = listOf(
    TabItem(Icons.Rounded.Link, "Shorten"),
    TabItem(Icons.AutoMirrored.Rounded.OpenInNew, "Detail"),
    TabItem(Icons.Rounded.BarChart, "Analytics"),
    TabItem(Icons.Rounded.Tune, "Settings"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val vm: MainViewModel = viewModel(factory = MainViewModel.factory())

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "lil·url",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Rounded.Logout, contentDescription = "Log out", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                )

                PrimaryTabRow(
                    selectedTabIndex = vm.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    TABS.forEachIndexed { index, tab ->
                        Tab(
                            selected = vm.selectedTab == index,
                            onClick = { vm.selectTab(index) },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(22.dp),
                                )
                            },
                        )
                    }
                }

                // Context chip pinned below tabs for Detail/Analytics/Settings
                if (vm.selectedTab in 1..3) {
                    val link = vm.selectedLink
                    if (link != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Link,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = link.shortUrl,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f),
                                )
                                AssistChip(
                                    onClick = { vm.goToShorten() },
                                    label = { Text("Switch", style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ),
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                    shape = RoundedCornerShape(8.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = vm.selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_transition",
        ) { tab ->
            when (tab) {
                0 -> ShortenScreen(mainVm = vm, modifier = Modifier.padding(innerPadding))
                1 -> LinkDetailScreen(mainVm = vm, modifier = Modifier.padding(innerPadding))
                2 -> AnalyticsScreen(mainVm = vm, modifier = Modifier.padding(innerPadding))
                else -> LinkSettingsScreen(mainVm = vm, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
