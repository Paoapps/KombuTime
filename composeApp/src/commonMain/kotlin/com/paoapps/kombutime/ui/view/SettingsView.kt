package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paoapps.kombutime.ui.theme.DANGER_COLOR
import com.paoapps.kombutime.ui.theme.DesignSystem
import com.paoapps.kombutime.utils.resolve
import com.paoapps.kombutime.viewmodel.SettingsViewModel
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.batch_settings
import kombutime.composeapp.generated.resources.delete_batch
import kombutime.composeapp.generated.resources.delete_brew_dialog_cancel
import kombutime.composeapp.generated.resources.delete_brew_dialog_confirm
import kombutime.composeapp.generated.resources.delete_brew_dialog_message
import kombutime.composeapp.generated.resources.delete_brew_dialog_title
import kombutime.composeapp.generated.resources.flavor
import kombutime.composeapp.generated.resources.flavor_dialog_custom
import kombutime.composeapp.generated.resources.flavor_dialog_no_flavor
import kombutime.composeapp.generated.resources.notification_settings
import kombutime.composeapp.generated.resources.tea_type
import kombutime.composeapp.generated.resources.tea_type_dialog_custom
import kombutime.composeapp.generated.resources.tea_type_dialog_no_tea_type
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    brewIndex: Int,
    viewModel: SettingsViewModel = viewModel(key = brewIndex.toString()) { SettingsViewModel(brewIndex) },
    onNavigateUp: () -> Unit = {}
) {
    val output by viewModel.output.collectAsState()
    val savedFlavors by viewModel.savedFlavors.collectAsState()
    val savedTeaTypes by viewModel.savedTeaTypes.collectAsState()
    val showDeleteConfirmation by viewModel.showDeleteConfirmation.collectAsState()

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
            // Date stepper card
            output.dateStepper?.let { stepper ->
                SettingsCard(title = null) {
                    ModernStepper(stepper)
                }
            }

            // Batch settings card
            SettingsCard(title = stringResource(Res.string.batch_settings)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
                ) {
                    // Tea type dropdown (only for first fermentation)
                    output.teaType?.let { currentTeaType ->
                        TeaTypeDropdown(
                            currentTeaType = currentTeaType,
                            savedTeaTypes = savedTeaTypes,
                            onTeaTypeChanged = { viewModel.updateTeaType(it) }
                        )
                    }

                    // Flavor dropdown (only for second fermentation)
                    output.flavor?.let { currentFlavor ->
                        FlavorDropdown(
                            currentFlavor = currentFlavor,
                            savedFlavors = savedFlavors,
                            onFlavorChanged = { viewModel.updateFlavor(it) }
                        )
                    }

                    output.brewSettingsSteppers.forEach { stepper ->
                        ModernStepper(stepper)
                    }
                }
            }

            // Notification settings card
            output.notificationTimeStepper?.let { stepper ->
                SettingsCard(title = stringResource(Res.string.notification_settings)) {
                    ModernStepper(stepper)
                }
            }

            // Delete button
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.medium))

            OutlinedButton(
                onClick = { viewModel.showDeleteConfirmation() },
                modifier = Modifier.fillMaxWidth(),
                shape = DesignSystem.CornerRadius.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DesignSystem.Colors.accentRed
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(Res.string.delete_batch),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.large))
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirmation() },
            title = { Text(stringResource(Res.string.delete_brew_dialog_title)) },
            text = { Text(stringResource(Res.string.delete_brew_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmDeleteBrew()
                        onNavigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DANGER_COLOR,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(Res.string.delete_brew_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) {
                    Text(stringResource(Res.string.delete_brew_dialog_cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
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
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DesignSystem.Colors.textPrimary
                )
            }
            content()
        }
    }
}

@Composable
fun ModernStepper(
    properties: SettingsViewModel.Output.Stepper
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = properties.label.resolve(),
            style = MaterialTheme.typography.bodyLarge,
            color = DesignSystem.Colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Minus button
            Surface(
                onClick = { properties.onDecrement() },
                shape = CircleShape,
                color = DesignSystem.Colors.accentBlue.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = DesignSystem.Colors.accentBlue,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }

            Text(
                text = properties.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DesignSystem.Colors.textPrimary,
                modifier = Modifier.padding(horizontal = DesignSystem.Spacing.medium)
            )

            // Plus button
            Surface(
                onClick = { properties.onIncrement() },
                shape = CircleShape,
                color = DesignSystem.Colors.accentBlue.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = DesignSystem.Colors.accentBlue,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
fun Stepper(
    properties: SettingsViewModel.Output.Stepper
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = properties.label.resolve(),
            style = MaterialTheme.typography.bodyLarge,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = { properties.onDecrement() }) {
                Text("-")
            }

            Text(
                text = properties.value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )

            Button(onClick = { properties.onIncrement() }) {
                Text("+")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaTypeDropdown(
    currentTeaType: String,
    savedTeaTypes: List<String>,
    onTeaTypeChanged: (String) -> Unit
) {
    val noTeaTypeOption = stringResource(Res.string.tea_type_dialog_no_tea_type)
    val customOption = stringResource(Res.string.tea_type_dialog_custom)

    var expanded by remember { mutableStateOf(false) }
    var selectedTeaType by remember(currentTeaType) {
        mutableStateOf(
            when {
                currentTeaType.isBlank() -> noTeaTypeOption
                savedTeaTypes.contains(currentTeaType) -> currentTeaType
                else -> customOption
            }
        )
    }
    var customTeaType by remember(currentTeaType) {
        mutableStateOf(if (currentTeaType.isNotBlank() && !savedTeaTypes.contains(currentTeaType)) currentTeaType else "")
    }

    val isCustom = selectedTeaType == customOption

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.tea_type),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = DesignSystem.Colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (isCustom) customTeaType else selectedTeaType,
                onValueChange = {
                    if (isCustom) {
                        customTeaType = it
                        onTeaTypeChanged(it)
                    }
                },
                readOnly = !isCustom,
                label = { Text(stringResource(Res.string.tea_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = DesignSystem.CornerRadius.medium
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
                        onTeaTypeChanged("")
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

                if (savedTeaTypes.isNotEmpty()) {
                    HorizontalDivider()

                    // Saved tea types
                    savedTeaTypes.forEach { teaType ->
                        DropdownMenuItem(
                            text = { Text(teaType) },
                            onClick = {
                                selectedTeaType = teaType
                                expanded = false
                                onTeaTypeChanged(teaType)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlavorDropdown(
    currentFlavor: String,
    savedFlavors: List<String>,
    onFlavorChanged: (String) -> Unit
) {
    val noFlavorOption = stringResource(Res.string.flavor_dialog_no_flavor)
    val customOption = stringResource(Res.string.flavor_dialog_custom)

    var expanded by remember { mutableStateOf(false) }
    var selectedFlavor by remember(currentFlavor) {
        mutableStateOf(
            when {
                currentFlavor.isBlank() -> noFlavorOption
                savedFlavors.contains(currentFlavor) -> currentFlavor
                else -> customOption
            }
        )
    }
    var customFlavor by remember(currentFlavor) {
        mutableStateOf(if (currentFlavor.isNotBlank() && !savedFlavors.contains(currentFlavor)) currentFlavor else "")
    }

    val isCustom = selectedFlavor == customOption

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.flavor),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = DesignSystem.Colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (isCustom) customFlavor else selectedFlavor,
                onValueChange = {
                    if (isCustom) {
                        customFlavor = it
                        onFlavorChanged(it)
                    }
                },
                readOnly = !isCustom,
                label = { Text(stringResource(Res.string.flavor)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = DesignSystem.CornerRadius.medium
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
                        onFlavorChanged("")
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

                // Custom option
                DropdownMenuItem(
                    text = { Text(customOption) },
                    onClick = {
                        selectedFlavor = customOption
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

                if (savedFlavors.isNotEmpty()) {
                    HorizontalDivider()

                    // Saved flavors
                    savedFlavors.forEach { flavor ->
                        DropdownMenuItem(
                            text = { Text(flavor) },
                            onClick = {
                                selectedFlavor = flavor
                                expanded = false
                                onFlavorChanged(flavor)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}
