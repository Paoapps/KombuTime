package com.paoapps.kombutime

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paoapps.kombutime.domain.Batch
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.ui.theme.AppTheme
import com.paoapps.kombutime.ui.view.BatchesView
import com.paoapps.kombutime.ui.view.SettingsView
import com.paoapps.kombutime.viewmodel.AppViewModel
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.module

val module = module {
    single { Model() }
}

enum class Screen(val title: ResourceStringDesc) {
    Batches(title = MR.strings.batches_title.desc()),
    Settings(title = MR.strings.settings.desc())
}

@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.title.localized()) },
        actions = actions,
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = MR.strings.back_button.desc().localized()
                    )
                }
            }
        }
    )
}

data class Notification(
    val id: Int,
    val time: LocalDateTime,
    val title: StringDesc,
    val message: StringDesc
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

    val namePrefix = MR.strings.batches_batch.desc().localized()

    LaunchedEffect(Unit) {
        viewModel.setScheduleNotifications(scheduleNotifications)
    }

    KoinApplication(application = {
        modules(module)
    }) {
        AppTheme {
            Scaffold(topBar = {
                AppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    actions = {
                        when(currentScreen) {
                            Screen.Batches -> {
                                Button(onClick = { viewModel.addBatch(namePrefix) }) {
                                    Text(MR.strings.batches_add.desc().localized())
                                }
                            }
                            Screen.Settings -> {}
                        }
                    }
                )
            }) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Batches.name,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    composable(route = Screen.Batches.name) {
                        BatchesView(
                            onOpenSettings = {
                                navController.navigate("${Screen.Settings.name}/$it")
                            }
                        )
                    }

                    composable(route = "${Screen.Settings.name}/{index}") {
                        val index = it.arguments?.getString("index")!!.toInt()
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
