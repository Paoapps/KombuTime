package com.paoapps.kombutime.model

import com.paoapps.kombutime.domain.Batch
import com.paoapps.kombutime.domain.BatchState
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.batches_batch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
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

    fun addBatch(namePrefix: String) {
        var index = 1
        while (true) {
            val suggestedName = "$namePrefix $index"
            if (_batches.value.none { it.settings.name == suggestedName }) {
                _batches.value += Batch(
                    name = suggestedName,
                    startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
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
}
