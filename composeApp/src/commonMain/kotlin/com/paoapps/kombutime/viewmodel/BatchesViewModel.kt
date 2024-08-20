package com.paoapps.kombutime.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.paoapps.kombutime.MR
import com.paoapps.kombutime.domain.Batch
import com.paoapps.kombutime.domain.BatchState
import com.paoapps.kombutime.model.Model
import com.paoapps.kombutime.ui.theme.FIRST_FERMENTATION_COLOR
import com.paoapps.kombutime.ui.theme.SECOND_FERMENTATION_COLOR
import com.paoapps.kombutime.utils.LocalDateFormat
import com.paoapps.kombutime.utils.formatDate
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.bottle
import kombutime.composeapp.generated.resources.jar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BatchesViewModel: ViewModel(), KoinComponent {

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

    private val _output = combine(nextDayTrigger, model.batches) { _, batches ->
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        Output(
            batches = batches.map { batch ->
                val startDate = batch.startDate
                val fermentationDays = when(batch.state) {
                    BatchState.FirstFermentation -> batch.settings.firstFermentationDays
                    is BatchState.SecondFermentation -> batch.settings.secondFermentationDays
                }
                val progress = (today - startDate).days.toDouble() / fermentationDays
                val remainingDays = fermentationDays - (today - startDate).days
                val endDate = startDate.plus(fermentationDays, DateTimeUnit.DAY)

                val body = if (remainingDays < 0) {
                    StringDesc.PluralFormatted(MR.plurals.overdue_days, -remainingDays, -remainingDays)
                } else {
                    StringDesc.PluralFormatted(MR.plurals.remaining_days, remainingDays, remainingDays)
                }
                val textColor = if (remainingDays < 0) {
                    Color.Red
                } else {
                    Color.Black
                }

                val color = when(batch.state) {
                    BatchState.FirstFermentation -> FIRST_FERMENTATION_COLOR
                    is BatchState.SecondFermentation -> SECOND_FERMENTATION_COLOR
                }
                Output.Batch(
                    icon = when(batch.state) {
                        BatchState.FirstFermentation -> Res.drawable.jar
                        is BatchState.SecondFermentation -> Res.drawable.bottle
                    },
                    title = when(val state = batch.state) {
                        BatchState.FirstFermentation -> batch.settings.name
                        is BatchState.SecondFermentation -> batch.settings.name // TODO: flavor "${batch.settings.name} - ${state.flavor}"
                    },
                    progressBar = Output.ProgressBar(
                        progress = progress.toFloat(),
                        header = when(batch.state) {
                            BatchState.FirstFermentation -> MR.strings.first_fermentation.desc()
                            is BatchState.SecondFermentation -> MR.strings.second_fermentation.desc()
                        },
                        body = body,
                        backgroundColor = color.copy(alpha = 0.5f),
                        progressColor = color,
                        textColor = textColor
                    ),
                    valueRows = listOf(
                        Output.ValueRow(
                            label = MR.strings.start_date.desc(),
                            value = formatDate(startDate, LocalDateFormat.LONG).desc()
                        ),
                        Output.ValueRow(
                            label = MR.strings.end_date.desc(),
                            value = formatDate(endDate, LocalDateFormat.LONG).desc()
                        )
                    ),
                    completeAction = {
                        when(batch.state) {
                            BatchState.FirstFermentation -> model.completeFirstFermentation(batches.indexOf(batch), "Blue Berry")
                            is BatchState.SecondFermentation -> model.complete(batches.indexOf(batch))
                        }

                    }
                )
            }
        )
    }

    val output: StateFlow<Output> = _output.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), Output(batches = emptyList()))

    data class Output(
        val batches: List<Batch>
    ) {
        data class Batch(
            val icon: DrawableResource,
            val title: String,
            val progressBar: ProgressBar,
            val valueRows: List<ValueRow> = emptyList(),
            val completeAction: () -> Unit
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true

                other as Batch

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
            val header: StringDesc,
            val body: StringDesc,
            val backgroundColor: Color,
            val progressColor: Color,
            val textColor: Color
        )

        data class ValueRow(
            val label: StringDesc,
            val value: StringDesc
        )
    }
}
