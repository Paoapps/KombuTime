package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.model.HistoryRepository
import com.paoapps.kombutime.viewmodel.HistoryViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.cancel
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
import org.jetbrains.compose.resources.pluralStringResource
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Flavor management card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToFlavors() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.manage_flavors),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(Res.string.manage_flavors_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }

        // Tea type management card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTeaTypes() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.manage_tea_types),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(Res.string.manage_tea_types_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
        
        // History Settings Section
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(Res.string.history_settings_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        // Save to history toggle
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.history_save_setting),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(Res.string.history_save_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = saveToHistory,
                    onCheckedChange = { historyRepository.setSaveToHistory(it) }
                )
            }
        }
        
        // Export history button
        if (historyOutput.historicalBrews.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.history_export),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val csv = historyViewModel.exportCSV()
                                onExportHistory(csv)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export CSV")
                        }
                        
                        Button(
                            onClick = {
                                val json = historyViewModel.exportJSON()
                                onExportHistory(json)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export JSON")
                        }
                    }
                }
            }
            
            // Clear history button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showClearDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.history_clear),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = stringResource(Res.string.history_clear_count, historyOutput.historicalBrews.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // Clear history confirmation dialog
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
                        color = MaterialTheme.colorScheme.error
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
