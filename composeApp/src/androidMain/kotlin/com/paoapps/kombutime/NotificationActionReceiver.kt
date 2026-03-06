package com.paoapps.kombutime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import com.paoapps.kombutime.model.Model
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {

    private val model: Model by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val brewNameNumber = intent.getIntExtra("brewNameNumber", -1)
        val notificationId = intent.getIntExtra("notificationId", -1)

        if (brewNameNumber == -1) {
            return
        }

        when (intent.action) {
            ACTION_COMPLETE -> {
                model.completeByNameNumber(brewNameNumber)
                // Dismiss the notification
                dismissNotification(context, notificationId)
            }
            ACTION_EXTEND -> {
                model.extendFermentationByNameNumber(brewNameNumber)
                // Dismiss the notification as it will be rescheduled
                dismissNotification(context, notificationId)
            }
        }
    }

    private fun dismissNotification(context: Context, notificationId: Int) {
        if (notificationId != -1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }
    }

    companion object {
        const val ACTION_COMPLETE = "com.paoapps.kombutime.ACTION_COMPLETE"
        const val ACTION_EXTEND = "com.paoapps.kombutime.ACTION_EXTEND"
    }
}
