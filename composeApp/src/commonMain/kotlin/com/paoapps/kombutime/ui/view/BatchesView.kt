package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.utils.resolve
import com.paoapps.kombutime.viewmodel.BatchesViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.complete
import kombutime.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BatchesView(
    viewModel: BatchesViewModel = viewModel { BatchesViewModel() },
    onOpenSettings: (Int) -> Unit = {}
) {
    val output by viewModel.output.collectAsState()

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        output.batches.forEachIndexed { index, batch ->
            if (index > 0) {
                HorizontalDivider()
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(painterResource(batch.icon), null)
                Text(batch.title)
            }

            ProgressBar(
                properties = batch.progressBar,
                modifier = Modifier.fillMaxWidth(),
            )

            batch.valueRows.forEach { row ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = row.label.resolve(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = row.value.resolve(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    onClick = { batch.completeAction() }
                ) {
                    Text(stringResource(Res.string.complete))
                }
                OutlinedButton(onClick = {
                    onOpenSettings(index)
                }) {
                    Text(stringResource(Res.string.settings))
                }
            }
        }
    }
}

@Composable
fun ProgressBar(
    properties: BatchesViewModel.Output.ProgressBar,
    modifier: Modifier = Modifier,
    clipShape: Shape = RoundedCornerShape(16.dp) // Adjusted based on the image
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(properties.backgroundColor)
    ) {
        if (properties.progress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(properties.progress)
                    .background(properties.progressColor)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = properties.textColor
                    )
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = properties.textColor
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = properties.header.resolve(),
                style = MaterialTheme.typography.bodyMedium,
                color = properties.textColor
            )
            Text(
                text = properties.body.resolve(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = properties.textColor
            )
        }
    }
}
