package com.paoapps.kombutime

import com.paoapps.kombutime.model.Model
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Helper object for iOS to handle notification actions
 * This bridges Swift code with Kotlin/Koin
 */
object NotificationActionHandler : KoinComponent {

    private val model: Model by inject()

    /**
     * Complete a brew by its name number
     * Called from iOS when user taps "Complete" on notification
     */
    fun completeBrewFromNotification(brewNameNumber: Int) {
        model.completeByNameNumber(brewNameNumber)
    }

    /**
     * Extend fermentation by 1 day
     * Called from iOS when user taps "Extend 1 Day" on notification
     */
    fun extendBrewFromNotification(brewNameNumber: Int) {
        model.extendFermentationByNameNumber(brewNameNumber)
    }
}
