package com.paoapps.kombutime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("notificationTitle") ?: "Notification"
        val message = intent.getStringExtra("notificationMessage") ?: "You have a new message"
        val brewNameNumber = intent.getIntExtra("brewNameNumber", -1)
        val notificationId = intent.getIntExtra("notificationId", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if necessary
        val channel = NotificationChannel(
            "your_channel_id",
            "Your Channel Name",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // Create action buttons
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COMPLETE
            putExtra("brewNameNumber", brewNameNumber)
            putExtra("notificationId", notificationId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            brewNameNumber * 10 + 1, // Unique request code
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val extendIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_EXTEND
            putExtra("brewNameNumber", brewNameNumber)
            putExtra("notificationId", notificationId)
        }
        val extendPendingIntent = PendingIntent.getBroadcast(
            context,
            brewNameNumber * 10 + 2, // Unique request code
            extendIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "your_channel_id")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_notification, "Complete", completePendingIntent)
            .addAction(R.drawable.ic_notification, "Extend 1 Day", extendPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
