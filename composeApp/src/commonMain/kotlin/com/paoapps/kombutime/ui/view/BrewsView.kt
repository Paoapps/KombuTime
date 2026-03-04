package com.paoapps.kombutime.ui.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.utils.resolve
import com.paoapps.kombutime.viewmodel.BrewsViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.complete
import kombutime.composeapp.generated.resources.empty_state_emoji
import kombutime.composeapp.generated.resources.empty_state_message
import kombutime.composeapp.generated.resources.empty_state_title
import kombutime.composeapp.generated.resources.flavor_dialog_cancel
import kombutime.composeapp.generated.resources.flavor_dialog_confirm
import kombutime.composeapp.generated.resources.flavor_dialog_custom
import kombutime.composeapp.generated.resources.flavor_dialog_message
import kombutime.composeapp.generated.resources.flavor_dialog_no_flavor
import kombutime.composeapp.generated.resources.flavor_dialog_placeholder
import kombutime.composeapp.generated.resources.flavor_dialog_title
import kombutime.composeapp.generated.resources.settings
import kombutime.composeapp.generated.resources.tea_type_dialog_cancel
import kombutime.composeapp.generated.resources.tea_type_dialog_confirm
import kombutime.composeapp.generated.resources.tea_type_dialog_custom
import kombutime.composeapp.generated.resources.tea_type_dialog_message
import kombutime.composeapp.generated.resources.tea_type_dialog_no_tea_type
import kombutime.composeapp.generated.resources.tea_type_dialog_placeholder
import kombutime.composeapp.generated.resources.tea_type_dialog_title
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewsView(
    viewModel: BrewsViewModel = viewModel { BrewsViewModel() },
    onOpenSettings: (Int) -> Unit = {},
    onAddBrew: () -> Unit = {}
) {
    val output by viewModel.output.collectAsState()
    val flavorDialogState by viewModel.flavorDialogState.collectAsState()
    val teaTypeDialogState by viewModel.teaTypeDialogState.collectAsState()
    val savedFlavors by viewModel.savedFlavors.collectAsState()
    val savedTeaTypes by viewModel.savedTeaTypes.collectAsState()

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (output.brews.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.empty_state_emoji),
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = stringResource(Res.string.empty_state_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(Res.string.empty_state_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        output.brews.forEachIndexed { index, brew ->
            if (index > 0) {
                HorizontalDivider()
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(painterResource(brew.icon), null)
                Text(brew.title)
            }

            ProgressBar(
                properties = brew.progressBar,
                modifier = Modifier.fillMaxWidth(),
            )

            brew.valueRows.forEach { row ->
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
                    onClick = { brew.completeAction() }
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

    // Flavor input dialog
    flavorDialogState?.let { dialogState ->
        val noFlavorOption = stringResource(Res.string.flavor_dialog_no_flavor)
        val customOption = stringResource(Res.string.flavor_dialog_custom)
        val placeholder = stringResource(Res.string.flavor_dialog_placeholder)

        var selectedFlavor by remember { mutableStateOf<String?>(noFlavorOption) }
        var customFlavor by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }

        val isCustom = selectedFlavor == customOption
        val finalFlavor = when (selectedFlavor) {
            noFlavorOption -> ""
            customOption -> customFlavor
            else -> selectedFlavor ?: ""
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissFlavorDialog() },
            title = { Text(stringResource(Res.string.flavor_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(Res.string.flavor_dialog_message))

                    // Exposed dropdown menu for saved flavors
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedFlavor ?: noFlavorOption,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // No flavor option
                            DropdownMenuItem(
                                text = { Text(noFlavorOption) },
                                onClick = {
                                    selectedFlavor = noFlavorOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )

                            HorizontalDivider()

                            // Custom option
                            DropdownMenuItem(
                                text = { Text(customOption) },
                                onClick = {
                                    selectedFlavor = customOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )

                            HorizontalDivider()

                            // Saved flavors
                            savedFlavors.forEach { flavor ->
                                DropdownMenuItem(
                                    text = { Text(flavor) },
                                    onClick = {
                                        selectedFlavor = flavor
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    // Custom flavor input (shown only when "Custom" is selected)
                    if (isCustom) {
                        TextField(
                            value = customFlavor,
                            onValueChange = { customFlavor = it },
                            placeholder = { Text(stringResource(Res.string.flavor_dialog_placeholder)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.completeFirstFermentation(dialogState.brewIndex, finalFlavor)
                    },
                    enabled = !isCustom || customFlavor.isNotBlank()
                ) {
                    Text(stringResource(Res.string.flavor_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissFlavorDialog() }) {
                    Text(stringResource(Res.string.flavor_dialog_cancel))
                }
            }
        )
    }

    // Tea type input dialog
    teaTypeDialogState?.let { dialogState ->
        val noTeaTypeOption = stringResource(Res.string.tea_type_dialog_no_tea_type)
        val customOption = stringResource(Res.string.tea_type_dialog_custom)
        val placeholder = stringResource(Res.string.tea_type_dialog_placeholder)

        var selectedTeaType by remember { mutableStateOf<String?>(noTeaTypeOption) }
        var customTeaType by remember { mutableStateOf("") }

        val isCustom = selectedTeaType == customOption
        val finalTeaType = when {
            selectedTeaType == noTeaTypeOption -> ""
            isCustom -> customTeaType
            else -> selectedTeaType ?: ""
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissTeaTypeDialog() },
            title = { Text(stringResource(Res.string.tea_type_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(Res.string.tea_type_dialog_message))

                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedTeaType ?: noTeaTypeOption,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // No tea type option
                            DropdownMenuItem(
                                text = { Text(noTeaTypeOption) },
                                onClick = {
                                    selectedTeaType = noTeaTypeOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )

                            // Custom option
                            DropdownMenuItem(
                                text = { Text(customOption) },
                                onClick = {
                                    selectedTeaType = customOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )

                            HorizontalDivider()

                            // Saved tea types
                            savedTeaTypes.forEach { teaType ->
                                DropdownMenuItem(
                                    text = { Text(teaType) },
                                    onClick = {
                                        selectedTeaType = teaType
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    // Custom tea type input (shown only when "Custom" is selected)
                    if (isCustom) {
                        TextField(
                            value = customTeaType,
                            onValueChange = { customTeaType = it },
                            placeholder = { Text(placeholder) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addBrewWithTeaType(dialogState.namePrefix, finalTeaType)
                    },
                    enabled = !isCustom || customTeaType.isNotBlank()
                ) {
                    Text(stringResource(Res.string.tea_type_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissTeaTypeDialog() }) {
                    Text(stringResource(Res.string.tea_type_dialog_cancel))
                }
            }
        )
    }
}

@Composable
fun ProgressBar(
    properties: BrewsViewModel.Output.ProgressBar,
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
