package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.model.HistoryRepository
import com.paoapps.kombutime.ui.theme.DesignSystem
import com.paoapps.kombutime.viewmodel.HistoryViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.history_clear
import kombutime.composeapp.generated.resources.history_clear_count
import kombutime.composeapp.generated.resources.history_clear_dialog_cancel
import kombutime.composeapp.generated.resources.history_clear_dialog_confirm
import kombutime.composeapp.generated.resources.history_clear_dialog_message
import kombutime.composeapp.generated.resources.history_clear_dialog_title
import kombutime.composeapp.generated.resources.history_export
import kombutime.composeapp.generated.resources.history_save_description
import kombutime.composeapp.generated.resources.history_save_setting
import kombutime.composeapp.generated.resources.history_settings_title
import kombutime.composeapp.generated.resources.manage_flavors
import kombutime.composeapp.generated.resources.manage_flavors_description
import kombutime.composeapp.generated.resources.manage_tea_types
import kombutime.composeapp.generated.resources.manage_tea_types_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun AppSettingsView(
    onNavigateToFlavors: () -> Unit = {},
    onNavigateToTeaTypes: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onExportHistory: (String) -> Unit = {}
) {
    val historyRepository: HistoryRepository = koinInject()
    val historyViewModel: HistoryViewModel = viewModel { HistoryViewModel() }
    val historyOutput by historyViewModel.output.collectAsState()
    val saveToHistory by historyRepository.saveToHistory.collectAsState(initial = true)

    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DesignSystem.Colors.backgroundPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(DesignSystem.Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
        ) {
            ModernSettingCard(
                title = stringResource(Res.string.manage_flavors),
                description = stringResource(Res.string.manage_flavors_description),
                onClick = onNavigateToFlavors
            )

            ModernSettingCard(
                title = stringResource(Res.string.manage_tea_types),
                description = stringResource(Res.string.manage_tea_types_description),
                onClick = onNavigateToTeaTypes
            )

            Card(
                modifier = Modifier
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
                    Text(
                        text = stringResource(Res.string.history_settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DesignSystem.Colors.textPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.history_save_setting),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = DesignSystem.Colors.textPrimary
                            )
                            Text(
                                text = stringResource(Res.string.history_save_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DesignSystem.Colors.textSecondary
                            )
                        }
                        Switch(
                            checked = saveToHistory,
                            onCheckedChange = { historyRepository.setSaveToHistory(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DesignSystem.Colors.accentGreen,
                                checkedTrackColor = DesignSystem.Colors.accentGreen.copy(alpha = 0.5f)
                            )
                        )
                    }

                    if (historyOutput.historicalBrews.isNotEmpty()) {
                        HorizontalDivider()

                        Text(
                            text = stringResource(Res.string.history_export),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = DesignSystem.Colors.textPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
                        ) {
                            Button(
                                onClick = {
                                    val csv = historyViewModel.exportCSV()
                                    onExportHistory(csv)
                                },
                                modifier = Modifier.weight(1f),
                                shape = DesignSystem.CornerRadius.medium
                            ) {
                                Text("Export CSV")
                            }

                            Button(
                                onClick = {
                                    val json = historyViewModel.exportJSON()
                                    onExportHistory(json)
                                },
                                modifier = Modifier.weight(1f),
                                shape = DesignSystem.CornerRadius.medium
                            ) {
                                Text("Export JSON")
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClearDialog = true }
                                .padding(vertical = DesignSystem.Spacing.small),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(Res.string.history_clear),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = DesignSystem.Colors.accentRed
                                )
                                Text(
                                    text = stringResource(Res.string.history_clear_count, historyOutput.historicalBrews.size),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DesignSystem.Colors.textSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = DesignSystem.Colors.accentRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.large))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(stringResource(Res.string.history_clear_dialog_title))
            },
            text = {
                Text(
                    stringResource(
                        Res.string.history_clear_dialog_message,
                        historyOutput.historicalBrews.size
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        historyViewModel.clearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.history_clear_dialog_confirm),
                        color = DesignSystem.Colors.accentRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(Res.string.history_clear_dialog_cancel))
                }
            }
        )
    }
}

@Composable
private fun ModernSettingCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = DesignSystem.Elevation.small,
                shape = DesignSystem.CornerRadius.large
            )
            .clickable(onClick = onClick),
        shape = DesignSystem.CornerRadius.large,
        colors = CardDefaults.cardColors(
            containerColor = DesignSystem.Colors.cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DesignSystem.Colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DesignSystem.Colors.textSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = DesignSystem.Colors.textTertiary
            )
        }
    }
}
