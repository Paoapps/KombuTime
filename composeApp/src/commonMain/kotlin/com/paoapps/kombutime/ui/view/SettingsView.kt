package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.ui.theme.DANGER_COLOR
import com.paoapps.kombutime.utils.resolve
import com.paoapps.kombutime.viewmodel.SettingsViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.batch_settings
import kombutime.composeapp.generated.resources.delete_batch
import kombutime.composeapp.generated.resources.notification_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsView(
    batchIndex: Int,
    viewModel: SettingsViewModel = viewModel(key = batchIndex.toString()) { SettingsViewModel(batchIndex) },
    onNavigateUp: () -> Unit = {}
) {
    val output by viewModel.output.collectAsState()

    Column(
        Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = output.title.resolve(),
                style = MaterialTheme.typography.headlineSmall,
            )

            output.dateStepper?.let { stepper ->
                Stepper(stepper)
            }

            HorizontalDivider()

            Text(
                text = stringResource(Res.string.batch_settings),
                style = MaterialTheme.typography.titleLarge,
            )

            output.batchSettingsSteppers.forEach { stepper ->
                Stepper(stepper)
            }

            HorizontalDivider()

            Text(
                text = stringResource(Res.string.notification_settings),
                style = MaterialTheme.typography.titleLarge,
            )

            output.notificationTimeStepper?.let { stepper ->
                Stepper(stepper)
            }

        }

        Button(
            onClick = {
                viewModel.deleteBatch()
                onNavigateUp()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = DANGER_COLOR,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(Res.string.delete_batch))
        }
    }
}

@Composable
fun Stepper(
    properties: SettingsViewModel.Output.Stepper
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = properties.label.resolve(),
            style = MaterialTheme.typography.bodyLarge,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = { properties.onDecrement() }) {
                Text("-")
            }

            Text(
                text = properties.value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )

            Button(onClick = { properties.onIncrement() }) {
                Text("+")
            }
        }
    }
}
