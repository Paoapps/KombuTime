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
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import com.paoapps.kombutime.MR
import com.paoapps.kombutime.viewmodel.BatchesViewModel
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.jetbrains.compose.resources.painterResource

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
                Divider()
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
                        text = row.label.localized(),
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = row.value.localized(),
                        style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)
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
                    Text(MR.strings.complete.desc().localized())
                }
                OutlinedButton(onClick = {
                    onOpenSettings(index)
                }) {
                    Text(MR.strings.settings.desc().localized())
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
                        style = MaterialTheme.typography.body2,
                        color = properties.textColor
                    )
                    Text(
                        text = "",
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
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
                text = properties.header.localized(),
                style = MaterialTheme.typography.body2,
                color = properties.textColor
            )
            Text(
                text = properties.body.localized(),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = properties.textColor
            )
        }
    }
}
