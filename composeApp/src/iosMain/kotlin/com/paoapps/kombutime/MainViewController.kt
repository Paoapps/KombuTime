package com.paoapps.kombutime

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(
    scheduleNotifications: (List<Notification>) -> Unit
) = ComposeUIViewController {
    App(
        scheduleNotifications = scheduleNotifications
    )
}
