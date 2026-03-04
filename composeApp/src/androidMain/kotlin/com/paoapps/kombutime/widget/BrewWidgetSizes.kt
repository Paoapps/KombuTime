package com.paoapps.kombutime.widget

import androidx.glance.appwidget.SizeMode
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object BrewWidgetSizes {
    /**
     * Small widget - shows most urgent brew
     */
    val Small = DpSize(120.dp, 80.dp)

    /**
     * Medium widget - shows 2-3 brews
     */
    val Medium = DpSize(180.dp, 180.dp)

    /**
     * Large widget - shows all brews (up to 4)
     */
    val Large = DpSize(250.dp, 300.dp)
}
