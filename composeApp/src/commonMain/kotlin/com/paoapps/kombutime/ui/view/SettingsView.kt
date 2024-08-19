package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.MR
import com.paoapps.kombutime.ui.theme.DANGER_COLOR
import com.paoapps.kombutime.viewmodel.SettingsViewModel
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

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
                text = output.title.localized(),
                style = MaterialTheme.typography.h5,
            )

            output.dateStepper?.let { stepper ->
                Stepper(stepper)
            }

            Divider()

            Text(
                text = MR.strings.batch_settings.desc().localized(),
                style = MaterialTheme.typography.h6,
            )

            output.batchSettingsSteppers.forEach { stepper ->
                Stepper(stepper)
            }

            Divider()

            Text(
                text = MR.strings.notification_settings.desc().localized(),
                style = MaterialTheme.typography.h6,
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
                backgroundColor = DANGER_COLOR,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = MR.strings.delete_batch.desc().localized())
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
            text = properties.label.localized(),
            style = MaterialTheme.typography.body1,
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
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            )

            Button(onClick = { properties.onIncrement() }) {
                Text("+")
            }
        }
    }
}
