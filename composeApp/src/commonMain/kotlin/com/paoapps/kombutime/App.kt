package com.paoapps.kombutime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.ui.theme.AppTheme
import com.paoapps.kombutime.ui.view.AppSettingsView
import com.paoapps.kombutime.ui.view.BrewsView
import com.paoapps.kombutime.ui.view.FlavorManagementView
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
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.module

val module = module {
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
    TopAppBar(
        title = { Text(stringResource(currentScreen.titleRes)) },
        actions = actions,
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        }
    )
}

data class Notification(
    val id: Int,
    val time: LocalDateTime,
    val title: String,
    val message: String
)

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
            // Blue background for entire screen (including status bar area)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1B264F))
            ) {
                // Main content with status bar padding
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
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
                                                    contentDescription = stringResource(Res.string.app_settings)
                                                )
                                            }
                                            Button(onClick = { brewsVM.checkIfShouldPromptForTeaType(namePrefix) }) {
                                                Text(stringResource(Res.string.brews_add))
                                            }
                                        }
                                        Screen.Settings -> {}
                                        Screen.AppSettings -> {}
                                        Screen.FlavorManagement -> {}
                                        Screen.TeaTypeManagement -> {}
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        // White background for content area
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(Color.White)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Brews.name,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable(route = Screen.Brews.name) {
                                    BrewsView(
                                        viewModel = brewsVM,
                                        onOpenSettings = {
                                            navController.navigate("${Screen.Settings.name}/$it")
                                        }
                                    )
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
                                        onNavigateUp = { navController.navigateUp() }
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
