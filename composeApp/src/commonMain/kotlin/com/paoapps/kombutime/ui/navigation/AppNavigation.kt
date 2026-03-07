package com.paoapps.kombutime.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.paoapps.kombutime.ui.view.BrewsView
import com.paoapps.kombutime.ui.view.HistoryView
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.tab_active
import kombutime.composeapp.generated.resources.tab_history
import org.jetbrains.compose.resources.stringResource

/**
 * Bottom tab navigation destinations
 */
private enum class TabDestination(
    val icon: ImageVector,
    val labelRes: org.jetbrains.compose.resources.StringResource
) {
    ACTIVE(Icons.Default.LocalDrink, Res.string.tab_active),
    HISTORY(Icons.Default.History, Res.string.tab_history)
}

/**
 * Main app navigation with bottom tab bar.
 * Shows Active Brews and History tabs.
 */
@Composable
fun AppNavigationWithTabs(
    modifier: Modifier = Modifier,
    content: @Composable (selectedTab: Int, onTabSelected: (Int) -> Unit) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                TabDestination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(stringResource(destination.labelRes))
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> BrewsView()
            1 -> HistoryView()
        }
    }
}
