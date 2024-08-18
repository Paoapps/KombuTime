package com.paoapps.kombutime.viewmodel

import androidx.lifecycle.ViewModel
import com.paoapps.kombutime.model.Model
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel: ViewModel(), KoinComponent {

    private val model: Model by inject()

    fun addBatch(namePrefix: String) {
        model.addBatch(namePrefix)
    }
}