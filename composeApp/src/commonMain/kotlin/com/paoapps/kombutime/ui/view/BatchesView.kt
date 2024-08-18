package com.paoapps.kombutime.ui.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paoapps.kombutime.viewmodel.BatchesViewModel
import dev.icerock.moko.resources.compose.localized
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.batches_add
import kombutime.composeapp.generated.resources.batches_batch
import kombutime.composeapp.generated.resources.batches_title
import kombutime.composeapp.generated.resources.complete
import kombutime.composeapp.generated.resources.jar
import kombutime.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BatchesView(viewModel: BatchesViewModel = androidx.lifecycle.viewmodel.compose.viewModel { BatchesViewModel() }) {
    val title by viewModel.title.collectAsState("")
    val output by viewModel.output.collectAsState()

    val namePrefix = stringResource(Res.string.batches_batch)

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.batches_title)) },
            actions = {
                Button(onClick = { viewModel.addBatch(namePrefix) }) {
                    Text(stringResource(Res.string.batches_add))
                }
            }
        )
    }) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
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
                    Image(painterResource(Res.drawable.jar), null)
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
                        Text(stringResource(Res.string.complete))
                    }
                    OutlinedButton(onClick = { viewModel.addBatch(namePrefix) }) {
                        Text(stringResource(Res.string.settings))
                    }
                }
            }

//            BatchesScreen()
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
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
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "",
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
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
                style = MaterialTheme.typography.body2
            )
            Text(
                text = properties.body.localized(),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
