package com.paoapps.kombutime.model

import com.paoapps.kombutime.MR
import com.paoapps.kombutime.Notification
import com.paoapps.kombutime.domain.Batch
import com.paoapps.kombutime.domain.BatchSettings
import com.paoapps.kombutime.domain.BatchState
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent

class Model: KoinComponent {

    private val jsonParser = Json

    private val storage: Settings = Settings()

    private val _batches = MutableStateFlow(storage["batches", "[]"].let {
        try {
            jsonParser.decodeFromString(ListSerializer(Batch.serializer()), it)
        } catch (e: Exception) {
            emptyList()
        }
    })
    val batches: Flow<List<Batch>> = _batches

    private val _notificationTime = MutableStateFlow(storage["notificationTime", (9 * 60 * 60)].let {
        LocalTime.fromSecondOfDay(it)
    })
    val notificationTime: Flow<LocalTime> = _notificationTime

    var scheduleNotifications: (List<Notification>) -> Unit = {}

    fun addBatch(namePrefix: String) {
        var index = 1
        while (true) {
            val suggestedName = "$namePrefix $index"
            if (_batches.value.none { it.settings.name == suggestedName }) {
                _batches.value += Batch(
                    startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    settings = (_batches.value.lastOrNull()?.settings ?: BatchSettings(
                        name = suggestedName
                    )).copy(name = suggestedName)
                )
                save()
                return
            }
            index++
        }
    }

    private fun save() {
        val batches = _batches.value
        storage["batches"] = jsonParser.encodeToString(ListSerializer(Batch.serializer()), batches)

        storage["notificationTime"] = _notificationTime.value.toSecondOfDay()

        scheduleNotifications(batches.map { batch ->
            Notification(
                id = batch.settings.name.hashCode() + batch.state.hashCode(),
                title = when (batch.state) {
                    BatchState.FirstFermentation -> MR.strings.notification_first_fermentation_title.desc()
                    is BatchState.SecondFermentation -> MR.strings.notification_second_fermentation_title.desc()
                },
                message = when (batch.state) {
                    is BatchState.FirstFermentation -> MR.strings.notification_first_fermentation_message.format(batch.settings.name)
                    is BatchState.SecondFermentation -> MR.strings.notification_second_fermentation_message.format(batch.settings.name)
                },
                time = batch.endDate.atTime(_notificationTime.value).toInstant(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
            )
        })
    }

    fun completeFirstFermentation(index: Int, flavor: String = "") {
        val batch = _batches.value[index]
        _batches.value = _batches.value.toMutableList().apply {
            set(index, batch.copy(
                state = BatchState.SecondFermentation(flavor),
                startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            ))
        }
        _batches.value += Batch(
            name = batch.settings.name,
            startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        )
        save()
    }

    fun complete(index: Int) {
        val batches = _batches.value.toMutableList()
        batches.removeAt(index)
        _batches.value = batches
        save()
    }

    fun incrementStartDate(batchIndex: Int) {
        val batch = _batches.value[batchIndex]
        _batches.value = _batches.value.toMutableList().apply {
            set(batchIndex, batch.copy(
                startDate = batch.startDate.plus(1, DateTimeUnit.DAY)
            ))
        }
        save()
    }

    fun decrementStartDate(batchIndex: Int) {
        val batch = _batches.value[batchIndex]
        _batches.value = _batches.value.toMutableList().apply {
            set(batchIndex, batch.copy(
                startDate = batch.startDate.plus(-1, DateTimeUnit.DAY)
            ))
        }
        save()
    }

    fun incrementFirstFermentationDays(batchIndex: Int) {
        val batch = _batches.value[batchIndex]
        val batchName = batch.settings.name
        val settings = batch.settings.copy(
            firstFermentationDays = batch.settings.firstFermentationDays + 1
        )
        _batches.value = _batches.value.map {
            if (it.settings.name == batchName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }

        save()
    }

    fun decrementFirstFermentationDays(batchIndex: Int) {
        val batch = _batches.value[batchIndex]
        val batchName = batch.settings.name
        val settings = batch.settings.copy(
            firstFermentationDays = batch.settings.firstFermentationDays - 1
        )
        _batches.value = _batches.value.map {
            if (it.settings.name == batchName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }
        save()
    }

    fun incrementSecondFermentationDays(batchIndex: Int) {
        val batch = _batches.value[batchIndex]
        val batchName = batch.settings.name
        val settings = batch.settings.copy(
            secondFermentationDays = batch.settings.secondFermentationDays + 1
        )
        _batches.value = _batches.value.map {
            if (it.settings.name == batchName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }
        save()
    }

    fun decrementSecondFermentationDays(batchIndex: Int) {
        val batch = _batches.value[batchIndex]
        val batchName = batch.settings.name
        val settings = batch.settings.copy(
            secondFermentationDays = batch.settings.secondFermentationDays - 1
        )
        _batches.value = _batches.value.map {
            if (it.settings.name == batchName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }
        save()
    }

    fun deleteBatch(batchIndex: Int) {
        val batches = _batches.value.toMutableList().apply {
            removeAt(batchIndex)
        }
        _batches.value = batches
        save()
    }

    fun setNotificationTime(time: LocalTime) {
        _notificationTime.value = time

        save()
    }
}
