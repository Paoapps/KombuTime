package com.paoapps.kombutime.ui.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.ui.theme.DesignSystem
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

    if (output.brews.isEmpty()) {
        // Modern empty state
        EmptyBrewsState()
    } else {
        // Modern brew list with cards
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(DesignSystem.Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
        ) {
            itemsIndexed(
                items = output.brews,
                key = { index, _ -> index }
            ) { index, brew ->
                ModernBrewCard(
                    brew = brew,
                    onComplete = { brew.completeAction() },
                    onOpenSettings = { onOpenSettings(index) }
                )
            }

            // Add bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
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

@Composable
private fun EmptyBrewsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DesignSystem.Spacing.huge),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.large)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Modern empty state icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(DesignSystem.Colors.backgroundTertiary),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.empty_state_emoji),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp)
            )
        }

        Spacer(modifier = Modifier.height(DesignSystem.Spacing.medium))

        Text(
            text = stringResource(Res.string.empty_state_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DesignSystem.Colors.textPrimary
        )

        Text(
            text = stringResource(Res.string.empty_state_message),
            style = MaterialTheme.typography.bodyLarge,
            color = DesignSystem.Colors.textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = DesignSystem.Spacing.extraLarge)
        )
    }
}

@Composable
private fun ModernBrewCard(
    brew: BrewsViewModel.Output.Brew,
    onComplete: () -> Unit,
    onOpenSettings: () -> Unit,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
        ) {
            // Header with brew name and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
            ) {
                // Icon container
                Image(
                    painter = painterResource(brew.icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(DesignSystem.Colors.textPrimary),
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = brew.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DesignSystem.Colors.textPrimary
                )
            }

            // Modern progress bar
            ModernProgressBar(
                properties = brew.progressBar,
                modifier = Modifier.fillMaxWidth()
            )

            // Info rows
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
            ) {
                brew.valueRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.label.resolve(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = DesignSystem.Colors.textSecondary
                        )
                        Text(
                            text = row.value.resolve(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = DesignSystem.Colors.textPrimary
                        )
                    }
                }
            }

            // Complete button with settings button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
            ) {
                // Complete button
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    shape = DesignSystem.CornerRadius.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesignSystem.Colors.accentGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.small))
                    Text(
                        text = stringResource(Res.string.complete),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Settings button - square
                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier,
                    shape = DesignSystem.CornerRadius.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DesignSystem.Colors.backgroundTertiary,
                        contentColor = DesignSystem.Colors.textSecondary
                    ),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.settings),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernProgressBar(
    properties: BrewsViewModel.Output.ProgressBar,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
    ) {
        // Progress info header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = properties.header.resolve(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = DesignSystem.Colors.textSecondary
            )
            Text(
                text = properties.body.resolve(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = properties.textColor
            )
        }

        // Modern progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(DesignSystem.CornerRadius.pill)
                .background(DesignSystem.Colors.progressBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(properties.progress)
                    .clip(DesignSystem.CornerRadius.pill)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                properties.progressColor,
                                properties.progressColor.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
        }
    }
}
