@file:OptIn(ExperimentalTime::class)

package com.paoapps.kombutime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paoapps.kombutime.domain.BrewState
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.utils.LocalDateFormat
import com.paoapps.kombutime.utils.UiText
import com.paoapps.kombutime.utils.formatDate
import com.paoapps.kombutime.utils.formatTime
import com.paoapps.kombutime.utils.toUiText
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.brews_batch
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
    private val brewIndex: Int
): ViewModel(), KoinComponent {

    private val model: Model by inject()

    val savedFlavors: StateFlow<List<String>> = model.savedFlavors.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val savedTeaTypes: StateFlow<List<String>> = model.savedTeaTypes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _output = combine(model.brews, model.notificationTime) { brews, notificationTime ->
        val brew = if (brews.size > brewIndex) brews[brewIndex] else return@combine Output()
        val namePrefix = org.jetbrains.compose.resources.getString(Res.string.brews_batch)
        val brewName = "$namePrefix ${brew.settings.nameNumber}"
        Output(
            title = brewName.toUiText() + " - ".toUiText() + when(brew.state) {
                is BrewState.FirstFermentation -> Res.string.first_fermentation.toUiText()
                is BrewState.SecondFermentation -> Res.string.second_fermentation.toUiText()
            },
            dateStepper = Output.Stepper(
                label = Res.string.start_date.toUiText(),
                value = formatDate(brew.startDate, LocalDateFormat.SHORT),
                onIncrement = {
                    model.incrementStartDate(brewIndex)
                },
                onDecrement = {
                    model.decrementStartDate(brewIndex)
                }
            ),
            brewSettingsSteppers = listOf(
                Output.Stepper(
                    label = Res.string.first_fermentation_days.toUiText(),
                    value = brew.settings.firstFermentationDays.toString(),
                    onIncrement = {
                        model.incrementFirstFermentationDays(brewIndex)
                    },
                    onDecrement = {
                        model.decrementFirstFermentationDays(brewIndex)
                    }
                ),
                Output.Stepper(
                    label = Res.string.second_fermentation_days.toUiText(),
                    value = brew.settings.secondFermentationDays.toString(),
                    onIncrement = {
                        model.incrementSecondFermentationDays(brewIndex)
                    },
                    onDecrement = {
                        model.decrementSecondFermentationDays(brewIndex)
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
            ),
            teaType = if (brew.state is BrewState.FirstFermentation) {
                (brew.state as BrewState.FirstFermentation).teaType
            } else null,
            flavor = if (brew.state is BrewState.SecondFermentation) {
                (brew.state as BrewState.SecondFermentation).flavor
            } else null
        )
    }

    val output: StateFlow<Output> = _output.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Output())

    fun updateTeaType(teaType: String) {
        model.updateBrewTeaType(brewIndex, teaType)
        if (teaType.isNotBlank()) {
            model.addSavedTeaType(teaType)
        }
    }

    fun updateFlavor(flavor: String) {
        model.updateBrewFlavor(brewIndex, flavor)
        if (flavor.isNotBlank()) {
            model.addSavedFlavor(flavor)
        }
    }

    data class Output(
        val title: UiText = "".toUiText(),
        val dateStepper: Stepper? = null,
        val brewSettingsSteppers: List<Stepper> = emptyList(),
        val notificationTimeStepper: Stepper? = null,
        val teaType: String? = null,
        val flavor: String? = null,
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

    fun deleteBrew() {
        model.deleteBrew(brewIndex)
    }
}
