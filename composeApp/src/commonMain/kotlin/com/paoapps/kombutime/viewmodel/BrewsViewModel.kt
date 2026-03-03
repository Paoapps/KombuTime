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
import kombutime.composeapp.generated.resources.end_date
import kombutime.composeapp.generated.resources.first_fermentation
import kombutime.composeapp.generated.resources.jar
import kombutime.composeapp.generated.resources.overdue_days
import kombutime.composeapp.generated.resources.remaining_days
import kombutime.composeapp.generated.resources.second_fermentation
import kombutime.composeapp.generated.resources.start_date
import kotlinx.coroutines.delay
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
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.ExperimentalTime

class BrewsViewModel: ViewModel(), KoinComponent {

    private val model: Model by inject()

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
        Output(
            brews = brews.map { brew ->
                val startDate = brew.startDate
                val fermentationDays = when(brew.state) {
                    BrewState.FirstFermentation -> brew.settings.firstFermentationDays
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
                    BrewState.FirstFermentation -> FIRST_FERMENTATION_COLOR
                    is BrewState.SecondFermentation -> SECOND_FERMENTATION_COLOR
                }
                Output.Brew(
                    icon = when(brew.state) {
                        BrewState.FirstFermentation -> Res.drawable.jar
                        is BrewState.SecondFermentation -> Res.drawable.bottle
                    },
                    title = when(val state = brew.state) {
                        BrewState.FirstFermentation -> brew.settings.name
                        is BrewState.SecondFermentation -> brew.settings.name // TODO: flavor "${brew.settings.name} - ${state.flavor}"
                    },
                    progressBar = Output.ProgressBar(
                        progress = progress.toFloat(),
                        header = when(brew.state) {
                            BrewState.FirstFermentation -> Res.string.first_fermentation.toUiText()
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
                            BrewState.FirstFermentation -> model.completeFirstFermentation(brews.indexOf(brew), "Blue Berry")
                            is BrewState.SecondFermentation -> model.complete(brews.indexOf(brew))
                        }

                    }
                )
            }
        )
    }

    val output: StateFlow<Output> = _output.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Output(brews = emptyList()))

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
