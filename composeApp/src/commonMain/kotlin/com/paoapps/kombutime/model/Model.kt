package com.paoapps.kombutime.model

import com.paoapps.kombutime.domain.Batch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent

class Model: KoinComponent {
    val batches: Flow<List<Batch>> = flowOf(listOf(Batch(LocalDate(2021, 1, 1))))
}
