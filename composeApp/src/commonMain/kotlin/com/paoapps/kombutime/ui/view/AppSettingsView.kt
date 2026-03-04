package com.paoapps.kombutime.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.manage_flavors
import kombutime.composeapp.generated.resources.manage_flavors_description
import kombutime.composeapp.generated.resources.manage_tea_types
import kombutime.composeapp.generated.resources.manage_tea_types_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppSettingsView(
    onNavigateToFlavors: () -> Unit = {},
    onNavigateToTeaTypes: () -> Unit = {},
    onNavigateUp: () -> Unit = {}
) {
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
    }
}
