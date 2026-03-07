package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.ui.theme.AppTheme
import com.paoapps.kombutime.viewmodel.HistoryViewModel
import kombutime.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.round

private fun Double.toOneDecimal(): String {
    return (round(this * 10) / 10).toString()
}

@Composable
fun HistoryView(
    viewModel: HistoryViewModel = viewModel { HistoryViewModel() },
    modifier: Modifier = Modifier
) {
    val output by viewModel.output.collectAsState()
    
    if (output.isEmpty) {
        HistoryEmptyState(modifier = modifier)
    } else {
        HistoryContent(
            output = output,
            modifier = modifier
        )
    }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(Res.string.history_empty_emoji),
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = stringResource(Res.string.history_empty_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(Res.string.history_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryContent(
    output: HistoryViewModel.Output,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Statistics header
        item {
            HistoryStatsCard(statistics = output.statistics)
        }
        
        // History items
        items(
            items = output.historicalBrews,
            key = { it.id }
        ) { brew ->
            HistoricalBrewCard(brew = brew)
        }
    }
}

@Composable
private fun HistoryStatsCard(
    statistics: com.paoapps.kombutime.model.HistoryStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.history_stats_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(Res.string.history_stats_total, statistics.totalBrews),
                style = MaterialTheme.typography.bodyMedium
            )
            
            statistics.firstBrewDate?.let { date ->
                Text(
                    text = stringResource(Res.string.history_stats_since, date.toString()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            statistics.mostUsedFlavor?.let { flavor ->
                Text(
                    text = stringResource(Res.string.history_stats_favorite, flavor),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (statistics.averageF1Days > 0) {
                Text(
                    text = stringResource(Res.string.history_stats_avg_f1, statistics.averageF1Days.toOneDecimal()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (statistics.averageF2Days > 0) {
                Text(
                    text = stringResource(Res.string.history_stats_avg_f2, statistics.averageF2Days.toOneDecimal()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun HistoricalBrewCard(
    brew: HistoryViewModel.HistoricalBrewItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Brew name with flavor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (brew.flavor.isNotEmpty()) {
                        "${brew.name} - ${brew.flavor}"
                    } else {
                        brew.name
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Tea type (if specified)
            if (brew.teaType.isNotEmpty()) {
                Text(
                    text = brew.teaType,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            HorizontalDivider()
            
            // Completed date (most prominent)
            Text(
                text = stringResource(Res.string.history_completed, brew.completedDate.toString()),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            // Fermentation timeline
            Text(
                text = stringResource(
                    Res.string.history_fermentation_timeline,
                    brew.firstFermentationDays,
                    brew.secondFermentationDays
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Start date (secondary info)
            Text(
                text = stringResource(Res.string.history_started, brew.startDate.toString()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun HistoryEmptyPreview() {
    AppTheme {
        Surface {
            HistoryEmptyState()
        }
    }
}
