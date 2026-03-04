@file:OptIn(ExperimentalTime::class)

package com.paoapps.kombutime.model

import com.paoapps.kombutime.Notification
import com.paoapps.kombutime.SettingsFactory
import com.paoapps.kombutime.WidgetUpdater
import com.paoapps.kombutime.domain.Brew
import com.paoapps.kombutime.domain.BrewSettings
import com.paoapps.kombutime.domain.BrewState
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kombutime.composeapp.generated.resources.Res
import kombutime.composeapp.generated.resources.notification_first_fermentation_message
import kombutime.composeapp.generated.resources.notification_first_fermentation_title
import kombutime.composeapp.generated.resources.notification_second_fermentation_message
import kombutime.composeapp.generated.resources.notification_second_fermentation_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime

// Default flavors to pre-populate
val DEFAULT_FLAVORS = listOf(
    "Blueberry",
    "Ginger",
    "Lemon",
    "Strawberry",
    "Mango",
    "Raspberry",
    "Peach",
    "Pineapple"
)

// Default tea types to pre-populate
val DEFAULT_TEA_TYPES = listOf(
    "Black Tea",
    "Green Tea",
    "Oolong Tea",
    "White Tea",
    "Pu-erh Tea",
    "Herbal Tea"
)

class Model: KoinComponent {

    private val jsonParser = Json
    private val scope = CoroutineScope(Dispatchers.Main)

    private val storage: Settings = SettingsFactory.createSettings()

    private val _brews = MutableStateFlow(storage["brews", "[]"].let {
        try {
            jsonParser.decodeFromString(ListSerializer(Brew.serializer()), it)
        } catch (e: Exception) {
            emptyList()
        }
    })
    val brews: Flow<List<Brew>> = _brews

    private val _notificationTime = MutableStateFlow(storage["notificationTime", (9 * 60 * 60)].let {
        LocalTime.fromSecondOfDay(it)
    })
    val notificationTime: Flow<LocalTime> = _notificationTime

    private val _savedFlavors = MutableStateFlow(storage["savedFlavors", "[]"].let {
        try {
            val loaded = jsonParser.decodeFromString(ListSerializer(String.serializer()), it)
            if (loaded.isEmpty()) DEFAULT_FLAVORS.sorted() else loaded
        } catch (e: Exception) {
            DEFAULT_FLAVORS.sorted()
        }
    })
    val savedFlavors: Flow<List<String>> = _savedFlavors

    private val _promptForFlavor = MutableStateFlow(storage["promptForFlavor", true])
    val promptForFlavor: Flow<Boolean> = _promptForFlavor

    private val _savedTeaTypes = MutableStateFlow(storage["savedTeaTypes", "[]"].let {
        try {
            val loaded = jsonParser.decodeFromString(ListSerializer(String.serializer()), it)
            if (loaded.isEmpty()) DEFAULT_TEA_TYPES.sorted() else loaded
        } catch (e: Exception) {
            DEFAULT_TEA_TYPES.sorted()
        }
    })
    val savedTeaTypes: Flow<List<String>> = _savedTeaTypes

    private val _promptForTeaType = MutableStateFlow(storage["promptForTeaType", true])
    val promptForTeaType: Flow<Boolean> = _promptForTeaType

    var scheduleNotifications: (List<Notification>) -> Unit = {}

    fun addBrew(namePrefix: String, teaType: String = "") {
        var index = 1
        while (true) {
            val suggestedName = "$namePrefix $index"
            if (_brews.value.none { it.settings.name == suggestedName }) {
                _brews.value += Brew(
                    startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    settings = (_brews.value.lastOrNull()?.settings ?: BrewSettings(
                        name = suggestedName
                    )).copy(name = suggestedName),
                    state = BrewState.FirstFermentation(teaType)
                )
                save()
                return
            }
            index++
        }
    }

    private fun save() {
        val brews = _brews.value
        storage["brews"] = jsonParser.encodeToString(ListSerializer(Brew.serializer()), brews)

        storage["notificationTime"] = _notificationTime.value.toSecondOfDay()

        scope.launch {
            val notifications = brews.map { brew ->
                Notification(
                    id = brew.settings.name.hashCode() + brew.state.hashCode(),
                    title = when (brew.state) {
                        is BrewState.FirstFermentation -> getString(Res.string.notification_first_fermentation_title)
                        is BrewState.SecondFermentation -> getString(Res.string.notification_second_fermentation_title)
                    },
                    message = when (brew.state) {
                        is BrewState.FirstFermentation -> getString(Res.string.notification_first_fermentation_message, brew.settings.name)
                        is BrewState.SecondFermentation -> getString(Res.string.notification_second_fermentation_message, brew.settings.name)
                    },
                    time = brew.endDate.atTime(_notificationTime.value).toInstant(TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
            scheduleNotifications(notifications)

            // Update widgets when brews change
            WidgetUpdater.updateWidgets()
        }
    }

    fun completeFirstFermentation(index: Int, flavor: String = "") {
        val brew = _brews.value[index]
        _brews.value = _brews.value.toMutableList().apply {
            set(index, brew.copy(
                state = BrewState.SecondFermentation(flavor),
                startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            ))
        }
        // Get tea type from completed brew to use for new brew
        val teaType = (brew.state as? BrewState.FirstFermentation)?.teaType ?: ""
        _brews.value += Brew(
            startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            settings = brew.settings,
            state = BrewState.FirstFermentation(teaType)
        )
        save()
    }

    fun complete(index: Int) {
        val brews = _brews.value.toMutableList()
        brews.removeAt(index)
        _brews.value = brews
        save()
    }

    fun incrementStartDate(brewIndex: Int) {
        val brew = _brews.value[brewIndex]
        _brews.value = _brews.value.toMutableList().apply {
            set(brewIndex, brew.copy(
                startDate = brew.startDate.plus(1, DateTimeUnit.DAY)
            ))
        }
        save()
    }

    fun decrementStartDate(brewIndex: Int) {
        val brew = _brews.value[brewIndex]
        _brews.value = _brews.value.toMutableList().apply {
            set(brewIndex, brew.copy(
                startDate = brew.startDate.plus(-1, DateTimeUnit.DAY)
            ))
        }
        save()
    }

    fun incrementFirstFermentationDays(brewIndex: Int) {
        val brew = _brews.value[brewIndex]
        val brewName = brew.settings.name
        val settings = brew.settings.copy(
            firstFermentationDays = brew.settings.firstFermentationDays + 1
        )
        _brews.value = _brews.value.map {
            if (it.settings.name == brewName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }

        save()
    }

    fun decrementFirstFermentationDays(brewIndex: Int) {
        val brew = _brews.value[brewIndex]
        val brewName = brew.settings.name
        val settings = brew.settings.copy(
            firstFermentationDays = brew.settings.firstFermentationDays - 1
        )
        _brews.value = _brews.value.map {
            if (it.settings.name == brewName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }
        save()
    }

    fun incrementSecondFermentationDays(brewIndex: Int) {
        val brew = _brews.value[brewIndex]
        val brewName = brew.settings.name
        val settings = brew.settings.copy(
            secondFermentationDays = brew.settings.secondFermentationDays + 1
        )
        _brews.value = _brews.value.map {
            if (it.settings.name == brewName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }
        save()
    }

    fun decrementSecondFermentationDays(brewIndex: Int) {
        val brew = _brews.value[brewIndex]
        val brewName = brew.settings.name
        val settings = brew.settings.copy(
            secondFermentationDays = brew.settings.secondFermentationDays - 1
        )
        _brews.value = _brews.value.map {
            if (it.settings.name == brewName) {
                it.copy(
                    settings = settings
                )
            } else {
                it
            }
        }
        save()
    }

    fun deleteBrew(brewIndex: Int) {
        val batches = _brews.value.toMutableList().apply {
            removeAt(brewIndex)
        }
        _brews.value = batches
        save()
    }

    fun setNotificationTime(time: LocalTime) {
        _notificationTime.value = time

        save()
    }

    fun addSavedFlavor(flavor: String) {
        if (flavor.isNotBlank() && !_savedFlavors.value.contains(flavor)) {
            _savedFlavors.value = (_savedFlavors.value + flavor).sorted()
            saveFlavors()
        }
    }

    fun updateSavedFlavor(oldFlavor: String, newFlavor: String) {
        if (newFlavor.isNotBlank()) {
            _savedFlavors.value = _savedFlavors.value.map {
                if (it == oldFlavor) newFlavor else it
            }.sorted()
            saveFlavors()
        }
    }

    fun deleteSavedFlavor(flavor: String) {
        _savedFlavors.value = _savedFlavors.value.filter { it != flavor }
        saveFlavors()
    }

    fun setPromptForFlavor(enabled: Boolean) {
        _promptForFlavor.value = enabled
        storage["promptForFlavor"] = enabled
    }

    private fun saveFlavors() {
        storage["savedFlavors"] = jsonParser.encodeToString(ListSerializer(String.serializer()), _savedFlavors.value)
    }

    // Tea type management methods
    fun addSavedTeaType(teaType: String) {
        if (teaType.isNotBlank() && !_savedTeaTypes.value.contains(teaType)) {
            _savedTeaTypes.value = (_savedTeaTypes.value + teaType).sorted()
            saveTeaTypes()
        }
    }

    fun updateSavedTeaType(oldTeaType: String, newTeaType: String) {
        if (newTeaType.isNotBlank()) {
            _savedTeaTypes.value = _savedTeaTypes.value.map {
                if (it == oldTeaType) newTeaType else it
            }.sorted()
            saveTeaTypes()
        }
    }

    fun deleteSavedTeaType(teaType: String) {
        _savedTeaTypes.value = _savedTeaTypes.value.filter { it != teaType }
        saveTeaTypes()
    }

    fun setPromptForTeaType(enabled: Boolean) {
        _promptForTeaType.value = enabled
        storage["promptForTeaType"] = enabled
    }

    private fun saveTeaTypes() {
        storage["savedTeaTypes"] = jsonParser.encodeToString(ListSerializer(String.serializer()), _savedTeaTypes.value)
    }
}

