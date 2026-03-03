package com.paoapps.kombutime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paoapps.kombutime.model.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppSettingsViewModel : ViewModel(), KoinComponent {

    private val model: Model by inject()

    val promptForFlavor: StateFlow<Boolean> = model.promptForFlavor.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val savedFlavors: StateFlow<List<String>> = model.savedFlavors.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _editingFlavor = MutableStateFlow<EditingFlavor?>(null)
    val editingFlavor: StateFlow<EditingFlavor?> = _editingFlavor

    fun setPromptForFlavor(enabled: Boolean) {
        model.setPromptForFlavor(enabled)
    }

    fun showAddFlavorDialog() {
        _editingFlavor.value = EditingFlavor(originalFlavor = null, currentName = "")
    }

    fun showEditFlavorDialog(flavor: String) {
        _editingFlavor.value = EditingFlavor(originalFlavor = flavor, currentName = flavor)
    }

    fun updateEditingFlavorName(name: String) {
        _editingFlavor.value = _editingFlavor.value?.copy(currentName = name)
    }

    fun saveFlavor() {
        _editingFlavor.value?.let { editing ->
            if (editing.currentName.isNotBlank()) {
                if (editing.originalFlavor == null) {
                    // Adding new flavor
                    model.addSavedFlavor(editing.currentName)
                } else {
                    // Updating existing flavor
                    model.updateSavedFlavor(editing.originalFlavor, editing.currentName)
                }
            }
            _editingFlavor.value = null
        }
    }

    fun deleteFlavor(flavor: String) {
        model.deleteSavedFlavor(flavor)
    }

    fun dismissFlavorDialog() {
        _editingFlavor.value = null
    }

    data class EditingFlavor(
        val originalFlavor: String?, // null for new flavor
        val currentName: String
    )
}
