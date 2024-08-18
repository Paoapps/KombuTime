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
import dev.icerock.moko.resources.desc.PluralStringDesc
import dev.icerock.moko.resources.desc.ResourceStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.batches_batch
import kombutime.composeapp.generated.resources.batches_unflavored
import kombutime.composeapp.generated.resources.bottle
import kombutime.composeapp.generated.resources.jar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.imageResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.days


class BatchesViewModel: ViewModel(), KoinComponent {

    private val model: Model by inject()

    private val _title = model.batches.map { "${it.size} Batches" }
    val title: Flow<String> = _title

    private val _output = model.batches.map { batches ->
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        Output(
            batches = batches.map { batch ->
                val startDate = batch.startDate
                val fermentationDays = when(val state = batch.state) {
                    BatchState.FirstFermentation -> batch.settings.firstFermentationDays
                    is BatchState.SecondFermentation -> batch.settings.secondFermentationDays
                }
                val progress = (today - startDate).days.toDouble() / fermentationDays
                val remainingDays = fermentationDays - (today - startDate).days
                val endDate = startDate.plus(fermentationDays, DateTimeUnit.DAY)

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
                        body = StringDesc.PluralFormatted(MR.plurals.remaining_days, remainingDays, remainingDays), // MR.plurals.remaining_days.desc(remainingDays),
                        backgroundColor = color.copy(alpha = 0.5f),
                        progressColor = color
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
                        when(val state = batch.state) {
                            BatchState.FirstFermentation -> model.completeFirstFermentation(batches.indexOf(batch), "Blue Berry")
                            is BatchState.SecondFermentation -> model.complete(batches.indexOf(batch))
                        }

                    }
//                    startDate =  formatDate(batch.startDate, LocalDateFormat.LONG)
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
        }

        data class ProgressBar(
            val progress: Float,
            val header: StringDesc,
            val body: StringDesc,
            val backgroundColor: Color,
            val progressColor: Color
        )

        data class ValueRow(
            val label: StringDesc,
            val value: StringDesc
        )
    }

    fun addBatch(namePrefix: String) {
        model.addBatch(namePrefix)
    }
}
