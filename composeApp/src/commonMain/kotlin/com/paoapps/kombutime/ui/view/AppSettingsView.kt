package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.viewmodel.AppSettingsViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.add_flavor
import kombutime.composeapp.generated.resources.cancel
import kombutime.composeapp.generated.resources.delete_flavor
import kombutime.composeapp.generated.resources.edit_flavor
import kombutime.composeapp.generated.resources.flavor_name
import kombutime.composeapp.generated.resources.flavor_prompt_description
import kombutime.composeapp.generated.resources.flavor_prompt_setting
import kombutime.composeapp.generated.resources.save
import kombutime.composeapp.generated.resources.saved_flavors
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppSettingsView(
    viewModel: AppSettingsViewModel = viewModel { AppSettingsViewModel() },
    onNavigateUp: () -> Unit = {}
) {
    val promptForFlavor by viewModel.promptForFlavor.collectAsState()
    val savedFlavors by viewModel.savedFlavors.collectAsState()
    val editingFlavor by viewModel.editingFlavor.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddFlavorDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_flavor))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Prompt for flavor setting
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
                            text = stringResource(Res.string.flavor_prompt_setting),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(Res.string.flavor_prompt_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = promptForFlavor,
                        onCheckedChange = { viewModel.setPromptForFlavor(it) }
                    )
                }
            }

            // Saved flavors section
            Text(
                text = stringResource(Res.string.saved_flavors),
                style = MaterialTheme.typography.titleLarge
            )

            savedFlavors.forEach { flavor ->
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
                        Text(
                            text = flavor,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = { viewModel.showEditFlavorDialog(flavor) }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = stringResource(Res.string.edit_flavor)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteFlavor(flavor) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(Res.string.delete_flavor)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Edit/Add flavor dialog
        editingFlavor?.let { editing ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissFlavorDialog() },
                title = {
                    Text(
                        if (editing.originalFlavor == null)
                            stringResource(Res.string.add_flavor)
                        else
                            stringResource(Res.string.edit_flavor)
                    )
                },
                text = {
                    TextField(
                        value = editing.currentName,
                        onValueChange = { viewModel.updateEditingFlavorName(it) },
                        label = { Text(stringResource(Res.string.flavor_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.saveFlavor() },
                        enabled = editing.currentName.isNotBlank()
                    ) {
                        Text(stringResource(Res.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissFlavorDialog() }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            )
        }
    }
}
