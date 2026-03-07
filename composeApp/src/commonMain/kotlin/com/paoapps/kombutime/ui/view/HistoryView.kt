package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.ui.theme.AppTheme
import com.paoapps.kombutime.ui.theme.DesignSystem
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignSystem.Spacing.huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.large)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Modern empty state icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(DesignSystem.Colors.backgroundTertiary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.history_empty_emoji),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp)
            )
        }

        Spacer(modifier = Modifier.height(DesignSystem.Spacing.medium))

        Text(
            text = stringResource(Res.string.history_empty_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DesignSystem.Colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(Res.string.history_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            color = DesignSystem.Colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = DesignSystem.Spacing.extraLarge)
        )
    }
}

@Composable
private fun HistoryContent(
    output: HistoryViewModel.Output,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(DesignSystem.Spacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
    ) {
        // Statistics header
        item {
            ModernHistoryStatsCard(statistics = output.statistics)
        }

        // History items
        items(
            items = output.historicalBrews,
            key = { it.id }
        ) { brew ->
            ModernHistoricalBrewCard(brew = brew)
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.large))
        }
    }
}

@Composable
private fun ModernHistoryStatsCard(
    statistics: com.paoapps.kombutime.model.HistoryStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = DesignSystem.Elevation.small,
                shape = DesignSystem.CornerRadius.large
            ),
        shape = DesignSystem.CornerRadius.large,
        colors = CardDefaults.cardColors(
            containerColor = DesignSystem.Colors.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.large)
        ) {
            // Header
            Text(
                text = stringResource(Res.string.history_stats_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DesignSystem.Colors.textPrimary
            )

            // Stats grid
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
            ) {
                StatRow(
                    icon = Icons.Default.LocalDrink,
                    iconColor = DesignSystem.Colors.accentBlue,
                    label = stringResource(Res.string.history_stats_total, statistics.totalBrews),
                    value = "${statistics.totalBrews}"
                )

                statistics.firstBrewDate?.let { date ->
                    StatRow(
                        icon = Icons.Default.CalendarToday,
                        iconColor = DesignSystem.Colors.accentGreen,
                        label = stringResource(Res.string.history_stats_since, date.toString()),
                        value = ""
                    )
                }

                statistics.mostUsedFlavor?.let { flavor ->
                    StatRow(
                        icon = Icons.Default.EmojiEvents,
                        iconColor = DesignSystem.Colors.accentOrange,
                        label = stringResource(Res.string.history_stats_favorite, flavor),
                        value = ""
                    )
                }

                if (statistics.averageF1Days > 0) {
                    StatRow(
                        icon = Icons.Default.Timer,
                        iconColor = DesignSystem.Colors.accentPurple,
                        label = stringResource(Res.string.history_stats_avg_f1, statistics.averageF1Days.toOneDecimal()),
                        value = ""
                    )
                }

                if (statistics.averageF2Days > 0) {
                    StatRow(
                        icon = Icons.Default.Timer,
                        iconColor = DesignSystem.Colors.accentPurple,
                        label = stringResource(Res.string.history_stats_avg_f2, statistics.averageF2Days.toOneDecimal()),
                        value = ""
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = DesignSystem.Colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
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
private fun ModernHistoricalBrewCard(
    brew: HistoryViewModel.HistoricalBrewItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = DesignSystem.Elevation.small,
                shape = DesignSystem.CornerRadius.large
            ),
        shape = DesignSystem.CornerRadius.large,
        colors = CardDefaults.cardColors(
            containerColor = DesignSystem.Colors.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
        ) {
            // Header row with brew name
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.extraSmall)
            ) {
                Text(
                    text = if (brew.flavor.isNotEmpty()) {
                        "${brew.name} - ${brew.flavor}"
                    } else {
                        brew.name
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DesignSystem.Colors.textPrimary
                )

                if (brew.teaType.isNotEmpty()) {
                    Text(
                        text = brew.teaType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DesignSystem.Colors.textSecondary
                    )
                }
            }

            // Completion date badge
            Row(
                modifier = Modifier
                    .clip(DesignSystem.CornerRadius.pill)
                    .background(DesignSystem.Colors.accentGreen.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DesignSystem.Colors.accentGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(Res.string.history_completed, brew.completedDate.toString()),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = DesignSystem.Colors.accentGreen
                )
            }

            // Fermentation info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // F1
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "F1",
                        style = MaterialTheme.typography.labelSmall,
                        color = DesignSystem.Colors.textSecondary
                    )
                    Text(
                        text = "${brew.firstFermentationDays} days",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DesignSystem.Colors.textPrimary
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DesignSystem.Colors.textTertiary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(20.dp)
                )

                // F2
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "F2",
                        style = MaterialTheme.typography.labelSmall,
                        color = DesignSystem.Colors.textSecondary
                    )
                    Text(
                        text = "${brew.secondFermentationDays} days",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DesignSystem.Colors.textPrimary
                    )
                }
            }

            // Start date
            Text(
                text = stringResource(Res.string.history_started, brew.startDate.toString()),
                style = MaterialTheme.typography.bodySmall,
                color = DesignSystem.Colors.textSecondary
            )
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
