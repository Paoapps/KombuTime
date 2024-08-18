package com.paoapps.kombutime.viewmodel

import androidx.lifecycle.ViewModel
import com.paoapps.kombutime.MR
import com.paoapps.kombutime.domain.BatchState
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.utils.LocalDateFormat
import com.paoapps.kombutime.utils.formatDate
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel(
    private val batchIndex: Int
): ViewModel(), KoinComponent {

    private val model: Model by inject()

    private val _output = model.batches.map { batches ->
        val batch = if (batches.size > batchIndex) batches[batchIndex] else return@map Output()
        Output(
            title = batch.settings.name.desc() + " ".desc() + when(batch.state) {
                BatchState.FirstFermentation -> MR.strings.first_fermentation.desc()
                is BatchState.SecondFermentation -> MR.strings.second_fermentation.desc()
            },
            dateStepper = Output.Stepper(
                label = MR.strings.start_date.desc(),
                value = formatDate(batch.startDate, LocalDateFormat.SHORT),
                onIncrement = {
                    model.incrementStartDate(batchIndex)
                },
                onDecrement = {
                    model.decrementStartDate(batchIndex)
                }
            ),
            batchSettingsSteppers = listOf(
                Output.Stepper(
                    label = MR.strings.first_fermentation_days.desc(),
                    value = batch.settings.firstFermentationDays.toString(),
                    onIncrement = {
                        model.incrementFirstFermentationDays(batchIndex)
                    },
                    onDecrement = {
                        model.decrementFirstFermentationDays(batchIndex)
                    }
                ),
                Output.Stepper(
                    label = MR.strings.second_fermentation_days.desc(),
                    value = batch.settings.secondFermentationDays.toString(),
                    onIncrement = {
                        model.incrementSecondFermentationDays(batchIndex)
                    },
                    onDecrement = {
                        model.decrementSecondFermentationDays(batchIndex)
                    }
                ),
            )
        )
    }

    val output: StateFlow<Output> = _output.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), Output())

    data class Output(
        val title: StringDesc = "".desc(),
        val dateStepper: Stepper? = null,
        val batchSettingsSteppers: List<Stepper> = emptyList()
    ) {
        data class Stepper(
            val label: StringDesc,
            val value: String,
            val onIncrement: () -> Unit,
            val onDecrement: () -> Unit,
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true

                other as Stepper

                if (label != other.label) return false
                if (value != other.value) return false

                return true
            }

            override fun hashCode(): Int {
                var result = label.hashCode()
                result = 31 * result + value.hashCode()
                return result
            }
        }
    }

    fun deleteBatch() {
        model.deleteBatch(batchIndex)
    }
}
