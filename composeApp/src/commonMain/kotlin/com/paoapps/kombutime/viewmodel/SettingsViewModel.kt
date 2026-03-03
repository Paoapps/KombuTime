@file:OptIn(ExperimentalTime::class)

package com.paoapps.kombutime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paoapps.kombutime.domain.BatchState
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.utils.LocalDateFormat
import com.paoapps.kombutime.utils.UiText
import com.paoapps.kombutime.utils.formatDate
import com.paoapps.kombutime.utils.formatTime
import com.paoapps.kombutime.utils.toUiText
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.first_fermentation
import kombutime.composeapp.generated.resources.first_fermentation_days
import kombutime.composeapp.generated.resources.notification_time
import kombutime.composeapp.generated.resources.second_fermentation
import kombutime.composeapp.generated.resources.second_fermentation_days
import kombutime.composeapp.generated.resources.start_date
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

class SettingsViewModel(
    private val batchIndex: Int
): ViewModel(), KoinComponent {

    private val model: Model by inject()

    private val _output = combine(model.batches, model.notificationTime) { batches, notificationTime ->
        val batch = if (batches.size > batchIndex) batches[batchIndex] else return@combine Output()
        Output(
            title = batch.settings.name.toUiText() + " - ".toUiText() + when(batch.state) {
                BatchState.FirstFermentation -> Res.string.first_fermentation.toUiText()
                is BatchState.SecondFermentation -> Res.string.second_fermentation.toUiText()
            },
            dateStepper = Output.Stepper(
                label = Res.string.start_date.toUiText(),
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
                    label = Res.string.first_fermentation_days.toUiText(),
                    value = batch.settings.firstFermentationDays.toString(),
                    onIncrement = {
                        model.incrementFirstFermentationDays(batchIndex)
                    },
                    onDecrement = {
                        model.decrementFirstFermentationDays(batchIndex)
                    }
                ),
                Output.Stepper(
                    label = Res.string.second_fermentation_days.toUiText(),
                    value = batch.settings.secondFermentationDays.toString(),
                    onIncrement = {
                        model.incrementSecondFermentationDays(batchIndex)
                    },
                    onDecrement = {
                        model.decrementSecondFermentationDays(batchIndex)
                    }
                ),
            ),
            notificationTimeStepper = Output.Stepper(
                label = Res.string.notification_time.toUiText(),
                value = formatTime(notificationTime),
                onIncrement = {
                    val date = LocalDate(2024, 1, 1)
                    val instant = notificationTime.atDate(date).toInstant(TimeZone.currentSystemDefault())
                    val updatedTime = instant.plus(30.minutes)
                    val updateLocalDateTime = updatedTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    model.setNotificationTime(updateLocalDateTime.time)
                },
                onDecrement = {
                    val date = LocalDate(2024, 1, 1)
                    val instant = notificationTime.atDate(date).toInstant(TimeZone.currentSystemDefault())
                    val updatedTime = instant.plus(-30.minutes)
                    val updateLocalDateTime = updatedTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    model.setNotificationTime(updateLocalDateTime.time)
                }
            )
        )
    }

    val output: StateFlow<Output> = _output.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Output())

    data class Output(
        val title: UiText = "".toUiText(),
        val dateStepper: Stepper? = null,
        val batchSettingsSteppers: List<Stepper> = emptyList(),
        val notificationTimeStepper: Stepper? = null,
    ) {
        data class Stepper(
            val label: UiText,
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
