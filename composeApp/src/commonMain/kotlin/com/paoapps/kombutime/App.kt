package com.paoapps.kombutime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.model.HistoryRepository
import com.paoapps.kombutime.ui.theme.AppTheme
import com.paoapps.kombutime.ui.theme.DesignSystem
import com.paoapps.kombutime.ui.view.AppSettingsView
import com.paoapps.kombutime.ui.view.BrewsView
import com.paoapps.kombutime.ui.view.FlavorManagementView
import com.paoapps.kombutime.ui.view.HistoryView
import com.paoapps.kombutime.ui.view.SettingsView
import com.paoapps.kombutime.ui.view.TeaTypeManagementView
import com.paoapps.kombutime.viewmodel.AppViewModel
import com.paoapps.kombutime.viewmodel.BrewsViewModel
import androidx.savedstate.read
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.app_settings
import kombutime.composeapp.generated.resources.app_settings_title
import kombutime.composeapp.generated.resources.back_button
import kombutime.composeapp.generated.resources.brews_add
import kombutime.composeapp.generated.resources.brews_batch
import kombutime.composeapp.generated.resources.brews_title
import kombutime.composeapp.generated.resources.manage_flavors
import kombutime.composeapp.generated.resources.manage_tea_types
import kombutime.composeapp.generated.resources.settings
import kombutime.composeapp.generated.resources.tab_active
import kombutime.composeapp.generated.resources.tab_history
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.module

val module = module {
    single { HistoryRepository() }
    single { Model() }
}

enum class Screen(val titleRes: StringResource) {
    Brews(titleRes = Res.string.brews_title),
    Settings(titleRes = Res.string.settings),
    AppSettings(titleRes = Res.string.app_settings_title),
    FlavorManagement(titleRes = Res.string.manage_flavors),
    TeaTypeManagement(titleRes = Res.string.manage_tea_types)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DesignSystem.Colors.backgroundSecondary,
        shadowElevation = DesignSystem.Elevation.small
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(currentScreen.titleRes),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DesignSystem.Colors.textPrimary
                )
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DesignSystem.Colors.backgroundSecondary,
                titleContentColor = DesignSystem.Colors.textPrimary,
                actionIconContentColor = DesignSystem.Colors.accentBlue
            ),
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back_button),
                            tint = DesignSystem.Colors.accentBlue
                        )
                    }
                }
            }
        )
    }
}

data class Notification(
    val id: Int,
    val time: LocalDateTime,
    val title: String,
    val message: String,
    val brewNameNumber: Int,
    val isSecondFermentation: Boolean
)

@Composable
private fun BottomNavigationBar(
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = DesignSystem.Elevation.medium,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ),
        color = DesignSystem.Colors.tabBarBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = DesignSystem.Spacing.extraLarge,
                    vertical = DesignSystem.Spacing.small
                )
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            TabBarItem(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                icon = if (selectedTab == 0) Icons.Filled.LocalDrink else Icons.Outlined.LocalDrink,
                label = stringResource(Res.string.tab_active),
                modifier = Modifier.weight(1f)
            )

            TabBarItem(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                icon = if (selectedTab == 1) Icons.Filled.History else Icons.Outlined.History,
                label = stringResource(Res.string.tab_history),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DesignSystem.Spacing.extraSmall),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) DesignSystem.Colors.tabBarSelected else DesignSystem.Colors.tabBarUnselected,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) DesignSystem.Colors.tabBarSelected else DesignSystem.Colors.tabBarUnselected,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
@Preview
fun App(
    scheduleNotifications: (List<Notification>) -> Unit = { _ -> },
    appViewModel: AppViewModel? = null,
    navController: NavHostController = rememberNavController(),
    brewsViewModel: BrewsViewModel? = null
) {
    KoinApplication(application = {
        modules(module)
    }) {
        // Create ViewModels inside KoinApplication context so Koin is initialized
        val viewModel = appViewModel ?: viewModel { AppViewModel() }
        val brewsVM = brewsViewModel ?: viewModel { BrewsViewModel() }

        // Tab selection state
        var selectedTab by remember { mutableStateOf(0) }

        // Get current back stack entry
        val backStackEntry by navController.currentBackStackEntryAsState()
        // Get the name of the current screen
        val route = backStackEntry?.destination?.route ?: Screen.Brews.name
        val currentScreen = Screen.entries.first { route.startsWith(it.name) }

        val namePrefix = stringResource(Res.string.brews_batch)

        LaunchedEffect(Unit) {
            viewModel.setScheduleNotifications(scheduleNotifications)
        }
        AppTheme {
            // Modern background for entire screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DesignSystem.Colors.backgroundPrimary)
            ) {
                // Main content
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            AppBar(
                                currentScreen = currentScreen,
                                canNavigateBack = navController.previousBackStackEntry != null,
                                navigateUp = { navController.navigateUp() },
                                actions = {
                                    when(currentScreen) {
                                        Screen.Brews -> {
                                            IconButton(onClick = { navController.navigate(Screen.AppSettings.name) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = stringResource(Res.string.app_settings),
                                                    tint = DesignSystem.Colors.accentBlue
                                                )
                                            }
                                        }
                                        Screen.Settings -> {}
                                        Screen.AppSettings -> {}
                                        Screen.FlavorManagement -> {}
                                        Screen.TeaTypeManagement -> {}
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            // Only show bottom nav on Brews screen
                            if (currentScreen == Screen.Brews) {
                                BottomNavigationBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { newTab -> selectedTab = newTab }
                                )
                            }
                        },
                        floatingActionButton = {
                            // Show FAB only on Active tab
                            if (currentScreen == Screen.Brews && selectedTab == 0) {
                                FloatingActionButton(
                                    onClick = { brewsVM.checkIfShouldPromptForTeaType(namePrefix) },
                                    containerColor = DesignSystem.Colors.accentBlue,
                                    contentColor = Color.White,
                                    shape = DesignSystem.CornerRadius.large
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(Res.string.brews_add)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        // Content area with modern background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(DesignSystem.Colors.backgroundPrimary)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Brews.name,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable(route = Screen.Brews.name) {
                                    when (selectedTab) {
                                        0 -> BrewsView(
                                            viewModel = brewsVM,
                                            onOpenSettings = {
                                                navController.navigate("${Screen.Settings.name}/$it")
                                            }
                                        )
                                        1 -> HistoryView()
                                    }
                                }

                                composable(route = "${Screen.Settings.name}/{index}") {
                                val index = it.arguments?.read { getString("index") }?.toInt() ?: 0
                                    SettingsView(
                                        brewIndex = index,
                                        onNavigateUp = { navController.navigateUp() }
                                    )
                                }

                                composable(route = Screen.AppSettings.name) {
                                    AppSettingsView(
                                        onNavigateToFlavors = { navController.navigate(Screen.FlavorManagement.name) },
                                        onNavigateToTeaTypes = { navController.navigate(Screen.TeaTypeManagement.name) },
                                        onNavigateUp = { navController.navigateUp() },
                                        onExportHistory = { content ->
                                            // Determine file type from content
                                            val (filename, mimeType) = if (content.trim().startsWith("[")) {
                                                "kombutime_history_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}.json" to "application/json"
                                            } else {
                                                "kombutime_history_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}.csv" to "text/csv"
                                            }
                                            com.paoapps.kombutime.utils.shareFile(content, filename, mimeType)
                                        }
                                    )
                                }

                                composable(route = Screen.FlavorManagement.name) {
                                    FlavorManagementView(
                                        onNavigateUp = { navController.navigateUp() }
                                    )
                                }

                                composable(route = Screen.TeaTypeManagement.name) {
                                    TeaTypeManagementView(
                                        onNavigateUp = { navController.navigateUp() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
