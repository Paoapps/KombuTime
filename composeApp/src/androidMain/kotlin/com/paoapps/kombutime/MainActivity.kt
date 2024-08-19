package com.paoapps.kombutime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDateTime
import java.util.Calendar
import android.provider.Settings
import android.widget.Toast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                putExtra("notificationTitle", notification.title.toString(context))
                putExtra("notificationMessage", notification.message.toString(context))
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
            set(Calendar.MONTH, localDateTime.monthNumber - 1)
            set(Calendar.DAY_OF_MONTH, localDateTime.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, localDateTime.hour)
            set(Calendar.MINUTE, localDateTime.minute)
            set(Calendar.SECOND, 0)
        }
    }

    private fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Exact alarms are allowed by default on Android 11 and below
        }
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms(context)) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                Toast.makeText(context, context.getString(R.string.toast_request_exact_alarm_permission), Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}