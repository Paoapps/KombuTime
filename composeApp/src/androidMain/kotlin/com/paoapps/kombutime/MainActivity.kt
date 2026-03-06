package com.paoapps.kombutime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import kotlinx.datetime.LocalDateTime
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize widget updater with app context
        WidgetUpdater.init(applicationContext)

        // Configure window for edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // Ensure white status bar text/icons
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            App(
                scheduleNotifications = { notifications ->
                    scheduleLocalNotifications(notifications, this)
                }
            )
        }
    }

    private fun scheduleLocalNotifications(notifications: List<Notification>, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!canScheduleExactAlarms(context)) {
            requestExactAlarmPermission(context)
            return
        }

        for (notification in notifications) {
            val triggerTime = localDateTimeToCalendar(notification.time).timeInMillis

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("notificationTitle", notification.title)
                putExtra("notificationMessage", notification.message)
                putExtra("brewNameNumber", notification.brewNameNumber)
                putExtra("notificationId", notification.id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notification.hashCode(), // Unique ID for each notification
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } catch (e: SecurityException) {
                // Handle the exception, potentially by notifying the user
                Toast.makeText(context, context.getString(R.string.toast_unable_to_schedule_alarm), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun localDateTimeToCalendar(localDateTime: LocalDateTime): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, localDateTime.year)
            set(Calendar.MONTH, localDateTime.month.ordinal)
            set(Calendar.DAY_OF_MONTH, localDateTime.day)
            set(Calendar.HOUR_OF_DAY, localDateTime.hour)
            set(Calendar.MINUTE, localDateTime.minute)
            set(Calendar.SECOND, 0)
        }
    }

    private fun canScheduleExactAlarms(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (!canScheduleExactAlarms(context)) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            Toast.makeText(context, context.getString(R.string.toast_request_exact_alarm_permission), Toast.LENGTH_LONG).show()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}