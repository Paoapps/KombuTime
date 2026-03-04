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

class TeaTypeManagementViewModel : ViewModel(), KoinComponent {

    private val model: Model by inject()

    val promptForTeaType: StateFlow<Boolean> = model.promptForTeaType.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val savedTeaTypes: StateFlow<List<String>> = model.savedTeaTypes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _editingTeaType = MutableStateFlow<EditingTeaType?>(null)
    val editingTeaType: StateFlow<EditingTeaType?> = _editingTeaType

    fun setPromptForTeaType(enabled: Boolean) {
        model.setPromptForTeaType(enabled)
    }

    fun showAddTeaTypeDialog() {
        _editingTeaType.value = EditingTeaType(originalTeaType = null, currentName = "")
    }

    fun showEditTeaTypeDialog(teaType: String) {
        _editingTeaType.value = EditingTeaType(originalTeaType = teaType, currentName = teaType)
    }

    fun updateEditingTeaTypeName(name: String) {
        _editingTeaType.value = _editingTeaType.value?.copy(currentName = name)
    }

    fun saveTeaType() {
        _editingTeaType.value?.let { editing ->
            if (editing.currentName.isNotBlank()) {
                if (editing.originalTeaType == null) {
                    // Adding new tea type
                    model.addSavedTeaType(editing.currentName)
                } else {
                    // Updating existing tea type
                    model.updateSavedTeaType(editing.originalTeaType, editing.currentName)
                }
            }
            _editingTeaType.value = null
        }
    }

    fun deleteTeaType(teaType: String) {
        model.deleteSavedTeaType(teaType)
    }

    fun dismissTeaTypeDialog() {
        _editingTeaType.value = null
    }

    data class EditingTeaType(
        val originalTeaType: String?,
        val currentName: String
    )
}
