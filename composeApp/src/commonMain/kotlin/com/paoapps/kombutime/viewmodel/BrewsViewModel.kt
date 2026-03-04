@file:OptIn(ExperimentalTime::class)

package com.paoapps.kombutime.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paoapps.kombutime.domain.Brew
import com.paoapps.kombutime.domain.BrewState
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.ui.theme.FIRST_FERMENTATION_COLOR
import com.paoapps.kombutime.ui.theme.SECOND_FERMENTATION_COLOR
import com.paoapps.kombutime.utils.LocalDateFormat
import com.paoapps.kombutime.utils.UiText
import com.paoapps.kombutime.utils.formatDate
import com.paoapps.kombutime.utils.toUiText
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.bottle
import kombutime.composeapp.generated.resources.brews_batch
import kombutime.composeapp.generated.resources.end_date
import kombutime.composeapp.generated.resources.first_fermentation
import kombutime.composeapp.generated.resources.jar
import kombutime.composeapp.generated.resources.overdue_days
import kombutime.composeapp.generated.resources.remaining_days
import kombutime.composeapp.generated.resources.second_fermentation
import kombutime.composeapp.generated.resources.start_date
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.ExperimentalTime

class BrewsViewModel: ViewModel(), KoinComponent {

    private val model: Model by inject()

    private val _flavorDialogState = MutableStateFlow<FlavorDialogState?>(null)
    val flavorDialogState: StateFlow<FlavorDialogState?> = _flavorDialogState

    private val _teaTypeDialogState = MutableStateFlow<TeaTypeDialogState?>(null)
    val teaTypeDialogState: StateFlow<TeaTypeDialogState?> = _teaTypeDialogState

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

    private val promptForFlavor: StateFlow<Boolean> = model.promptForFlavor.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    private val promptForTeaType: StateFlow<Boolean> = model.promptForTeaType.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    private val nextDayTrigger = flow<Unit> {
        emit(Unit)
        while (true) {
            val now = Clock.System.now()
            val startOfNextDay =
                now.toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(1, DateTimeUnit.DAY)
                    .atStartOfDayIn(TimeZone.currentSystemDefault())
            val durationUntilNextDay = startOfNextDay - now
            delay(durationUntilNextDay)
            emit(Unit)
        }
    }

    private val _output = combine(nextDayTrigger, model.brews) { _, brews ->
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val namePrefix = getString(Res.string.brews_batch)
        Output(
            brews = brews.map { brew ->
                val startDate = brew.startDate
                val fermentationDays = when(brew.state) {
                    is BrewState.FirstFermentation -> brew.settings.firstFermentationDays
                    is BrewState.SecondFermentation -> brew.settings.secondFermentationDays
                }
                val progress = (today - startDate).days.toDouble() / fermentationDays
                val remainingDays = fermentationDays - (today - startDate).days
                val endDate = startDate.plus(fermentationDays, DateTimeUnit.DAY)

                val body = if (remainingDays < 0) {
                    Res.plurals.overdue_days.toUiText(-remainingDays, -remainingDays)
                } else {
                    Res.plurals.remaining_days.toUiText(remainingDays, remainingDays)
                }
                val textColor = if (remainingDays < 0) {
                    Color.Red
                } else {
                    Color.Black
                }

                val color = when(brew.state) {
                    is BrewState.FirstFermentation -> FIRST_FERMENTATION_COLOR
                    is BrewState.SecondFermentation -> SECOND_FERMENTATION_COLOR
                }
                Output.Brew(
                    icon = when(brew.state) {
                        is BrewState.FirstFermentation -> Res.drawable.jar
                        is BrewState.SecondFermentation -> Res.drawable.bottle
                    },
                    title = when(val state = brew.state) {
                        is BrewState.FirstFermentation -> {
                            val brewName = "$namePrefix ${brew.settings.nameNumber}"
                            if (state.teaType.isNotBlank()) {
                                "$brewName - ${state.teaType}"
                            } else {
                                brewName
                            }
                        }
                        is BrewState.SecondFermentation -> {
                            val brewName = "$namePrefix ${brew.settings.nameNumber}"
                            if (state.flavor.isNotBlank()) {
                                "$brewName - ${state.flavor}"
                            } else {
                                brewName
                            }
                        }
                    },
                    progressBar = Output.ProgressBar(
                        progress = progress.toFloat(),
                        header = when(brew.state) {
                            is BrewState.FirstFermentation -> Res.string.first_fermentation.toUiText()
                            is BrewState.SecondFermentation -> Res.string.second_fermentation.toUiText()
                        },
                        body = body,
                        backgroundColor = color.copy(alpha = 0.5f),
                        progressColor = color,
                        textColor = textColor
                    ),
                    valueRows = listOf(
                        Output.ValueRow(
                            label = Res.string.start_date.toUiText(),
                            value = formatDate(startDate, LocalDateFormat.LONG).toUiText()
                        ),
                        Output.ValueRow(
                            label = Res.string.end_date.toUiText(),
                            value = formatDate(endDate, LocalDateFormat.LONG).toUiText()
                        )
                    ),
                    completeAction = {
                        when(brew.state) {
                            is BrewState.FirstFermentation -> {
                                val brewIndex = brews.indexOf(brew)
                                // Check if we should show flavor dialog
                                if (promptForFlavor.value) {
                                    _flavorDialogState.value = FlavorDialogState(brewIndex = brewIndex)
                                } else {
                                    // Complete without asking for flavor
                                    model.completeFirstFermentation(brewIndex, "")
                                }
                            }
                            is BrewState.SecondFermentation -> model.complete(brews.indexOf(brew))
                        }

                    }
                )
            }
        )
    }

    val output: StateFlow<Output> = _output.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Output(brews = emptyList()))

    fun completeFirstFermentation(brewIndex: Int, flavor: String) {
        model.completeFirstFermentation(brewIndex, flavor)
        // Add to saved flavors if not empty
        if (flavor.isNotBlank()) {
            model.addSavedFlavor(flavor)
        }
        _flavorDialogState.value = null
    }

    fun dismissFlavorDialog() {
        _flavorDialogState.value = null
    }

    fun showTeaTypeDialog(namePrefix: String) {
        _teaTypeDialogState.value = TeaTypeDialogState(namePrefix = namePrefix)
    }

    fun addBrewWithTeaType(namePrefix: String, teaType: String) {
        model.addBrew(namePrefix, teaType)
        // Add to saved tea types if not empty
        if (teaType.isNotBlank()) {
            model.addSavedTeaType(teaType)
        }
        _teaTypeDialogState.value = null
    }

    fun dismissTeaTypeDialog() {
        _teaTypeDialogState.value = null
    }

    fun checkIfShouldPromptForTeaType(namePrefix: String) {
        if (promptForTeaType.value) {
            showTeaTypeDialog(namePrefix)
        } else {
            model.addBrew(namePrefix, "")
        }
    }

    data class FlavorDialogState(
        val brewIndex: Int
    )

    data class TeaTypeDialogState(
        val namePrefix: String
    )

    data class Output(
        val brews: List<Brew>
    ) {
        data class Brew(
            val icon: DrawableResource,
            val title: String,
            val progressBar: ProgressBar,
            val valueRows: List<ValueRow> = emptyList(),
            val completeAction: () -> Unit
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true

                other as Brew

                if (icon != other.icon) return false
                if (title != other.title) return false
                if (progressBar != other.progressBar) return false
                if (valueRows != other.valueRows) return false

                return true
            }

            override fun hashCode(): Int {
                var result = icon.hashCode()
                result = 31 * result + title.hashCode()
                result = 31 * result + progressBar.hashCode()
                result = 31 * result + valueRows.hashCode()
                return result
            }
        }

        data class ProgressBar(
            val progress: Float,
            val header: UiText,
            val body: UiText,
            val backgroundColor: Color,
            val progressColor: Color,
            val textColor: Color
        )

        data class ValueRow(
            val label: UiText,
            val value: UiText
        )
    }
}
