package com.paoapps.kombutime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.utils.LocalDateFormat
import com.paoapps.kombutime.utils.formatDate
import com.russhwolf.settings.Settings
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.compose_multiplatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.compose.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val module = module {
    single { Model() }
}

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(module)
    }) {
        MaterialTheme {
            var showContent by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                BatchesScreen()
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }

    }
}

class BatchesViewModel: ViewModel(), KoinComponent {

    private val model: Model by inject()

    private val _title = model.batches.map { "${it.size} Batches" }
    val title: Flow<String> = _title

    private val _output = model.batches.map { batches ->
        Output(
            batches = batches.map { batch ->
                Output.Batch(
                    startDate =  formatDate(batch.startDate, LocalDateFormat.LONG)
                )
            }
        )
    }

    val output: StateFlow<Output> = _output.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), Output(batches = emptyList()))

    data class Output(
        val batches: List<Batch>
    ) {
        data class Batch(
            val startDate: String
        )

    }
}

@Composable
fun BatchesScreen(viewModel: BatchesViewModel = viewModel { BatchesViewModel() }) {
    val title by viewModel.title.collectAsState("")
    val output by viewModel.output.collectAsState()
    Text(title)
    Text(stringResource(MR.strings.my_string))

    output.batches.forEach { batch ->
        Text(batch.startDate)
    }
}