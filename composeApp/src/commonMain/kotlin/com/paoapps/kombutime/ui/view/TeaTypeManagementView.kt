package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.background
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.ui.theme.DesignSystem
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
        containerColor = DesignSystem.Colors.backgroundPrimary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTeaTypeDialog() },
                containerColor = DesignSystem.Colors.accentBlue
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(Res.string.add_tea_type),
                    tint = DesignSystem.Colors.Light.backgroundPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DesignSystem.Colors.backgroundPrimary)
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignSystem.Spacing.screenPadding)
                    .padding(top = DesignSystem.Spacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
            ) {
                // Prompt for tea type setting
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignSystem.Spacing.cardPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.tea_type_prompt_setting),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DesignSystem.Colors.textPrimary
                            )
                            Text(
                                text = stringResource(Res.string.tea_type_prompt_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DesignSystem.Colors.textSecondary
                            )
                        }
                        Switch(
                            checked = promptForTeaType,
                            onCheckedChange = { viewModel.setPromptForTeaType(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DesignSystem.Colors.accentGreen,
                                checkedTrackColor = DesignSystem.Colors.accentGreen.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // Saved tea types section
                Text(
                    text = stringResource(Res.string.saved_tea_types),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DesignSystem.Colors.textPrimary
                )

                savedTeaTypes.forEach { teaType ->
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignSystem.Spacing.cardPadding),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = teaType,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = DesignSystem.Colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.extraSmall)
                            ) {
                                IconButton(onClick = { viewModel.showEditTeaTypeDialog(teaType) }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = stringResource(Res.string.edit_tea_type),
                                        tint = DesignSystem.Colors.accentBlue
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteTeaType(teaType) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(Res.string.delete_tea_type),
                                        tint = DesignSystem.Colors.accentRed
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
                        enabled = editing.currentName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DesignSystem.Colors.accentBlue,
                            contentColor = Color.White
                        )
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
