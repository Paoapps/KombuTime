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
import com.paoapps.kombutime.viewmodel.TeaTypeManagementViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.add_tea_type
import kombutime.composeapp.generated.resources.cancel
import kombutime.composeapp.generated.resources.delete_tea_type
import kombutime.composeapp.generated.resources.edit_tea_type
import kombutime.composeapp.generated.resources.tea_type_name
import kombutime.composeapp.generated.resources.tea_type_prompt_description
import kombutime.composeapp.generated.resources.tea_type_prompt_setting
import kombutime.composeapp.generated.resources.save
import kombutime.composeapp.generated.resources.saved_tea_types
import org.jetbrains.compose.resources.stringResource

@Composable
fun TeaTypeManagementView(
    viewModel: TeaTypeManagementViewModel = viewModel { TeaTypeManagementViewModel() },
    onNavigateUp: () -> Unit = {}
) {
    val promptForTeaType by viewModel.promptForTeaType.collectAsState()
    val savedTeaTypes by viewModel.savedTeaTypes.collectAsState()
    val editingTeaType by viewModel.editingTeaType.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTeaTypeDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_tea_type))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Prompt for tea type setting
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
                                text = stringResource(Res.string.tea_type_prompt_setting),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(Res.string.tea_type_prompt_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = promptForTeaType,
                            onCheckedChange = { viewModel.setPromptForTeaType(it) }
                        )
                    }
                }

                // Saved tea types section
                Text(
                    text = stringResource(Res.string.saved_tea_types),
                    style = MaterialTheme.typography.titleLarge
                )

                savedTeaTypes.forEach { teaType ->
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
                                text = teaType,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(onClick = { viewModel.showEditTeaTypeDialog(teaType) }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = stringResource(Res.string.edit_tea_type)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteTeaType(teaType) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(Res.string.delete_tea_type)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit/Add tea type dialog
        editingTeaType?.let { editing ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissTeaTypeDialog() },
                title = {
                    Text(
                        if (editing.originalTeaType == null)
                            stringResource(Res.string.add_tea_type)
                        else
                            stringResource(Res.string.edit_tea_type)
                    )
                },
                text = {
                    TextField(
                        value = editing.currentName,
                        onValueChange = { viewModel.updateEditingTeaTypeName(it) },
                        label = { Text(stringResource(Res.string.tea_type_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.saveTeaType() },
                        enabled = editing.currentName.isNotBlank()
                    ) {
                        Text(stringResource(Res.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissTeaTypeDialog() }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            )
        }
    }
}
