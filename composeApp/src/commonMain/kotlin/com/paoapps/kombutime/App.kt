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
import com.paoapps.kombutime.ui.view.BatchesView
import com.paoapps.kombutime.ui.view.SettingsView
import com.paoapps.kombutime.viewmodel.AppViewModel
import androidx.savedstate.read
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.back_button
import kombutime.composeapp.generated.resources.batches_add
import kombutime.composeapp.generated.resources.batches_batch
import kombutime.composeapp.generated.resources.batches_title
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
    Batches(titleRes = Res.string.batches_title),
    Settings(titleRes = Res.string.settings)
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
    viewModel: AppViewModel = viewModel { AppViewModel() },
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val route = backStackEntry?.destination?.route ?: Screen.Batches.name
    val currentScreen = Screen.entries.first { route.startsWith(it.name) }

    val namePrefix = stringResource(Res.string.batches_batch)

    LaunchedEffect(Unit) {
        viewModel.setScheduleNotifications(scheduleNotifications)
    }

    KoinApplication(application = {
        modules(module)
    }) {
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
                                        Screen.Batches -> {
                                            Button(onClick = { viewModel.addBatch(namePrefix) }) {
                                                Text(stringResource(Res.string.batches_add))
                                            }
                                        }
                                        Screen.Settings -> {}
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
                                startDestination = Screen.Batches.name,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable(route = Screen.Batches.name) {
                                    BatchesView(
                                        onOpenSettings = {
                                            navController.navigate("${Screen.Settings.name}/$it")
                                        }
                                    )
                                }

                                composable(route = "${Screen.Settings.name}/{index}") {
                                val index = it.arguments?.read { getString("index") }?.toInt() ?: 0
                                    SettingsView(
                                        batchIndex = index,
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
