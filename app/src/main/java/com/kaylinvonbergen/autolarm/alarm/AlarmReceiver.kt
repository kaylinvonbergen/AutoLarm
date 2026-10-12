
package com.kaylinvonbergen.autolarm.alarm

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

import com.kaylinvonbergen.autolarm.MainActivity
import com.kaylinvonbergen.autolarm.R
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "autolarm_alarm_channel"

        fun createNotificationChannel(context: Context) {
            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_ID,
                "AutoLarm Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled AutoLarm alarms"
                enableVibration(true)
            }

            manager.createNotificationChannel(channel)
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (intent.action != AlarmScheduler.ACTION_FIRE) {
            return
        }

        val alarmId = intent.getStringExtra(
            AlarmScheduler.EXTRA_ALARM_ID
        ) ?: return

        val trigger = intent.getLongExtra(
            AlarmScheduler.EXTRA_TRIGGER,
            -1L
        )

        val scheduler = AlarmScheduler(context)

        // Ignore canceled or obsolete alarm occurrences.
        val valid = scheduler.handleTriggeredAlarm(
            alarmId,
            trigger
        )

        if (!valid) {
            Log.d("AutoLarm", "Ignored obsolete alarm")
            return
        }

        Log.d("AutoLarm", "Alarm fired: $alarmId")

        createNotificationChannel(context)

        // Android 13+ requires runtime notification permission.
        if (
            Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(
                "AutoLarm",
                "Alarm fired, but notification permission denied"
            )
            return
        }

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val openAppIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            alarmId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )


        val notification = Notification.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_stat_autolarm)
            .setContentTitle("Time to get up!")
            .setContentText(
                "Your AutoLarm wake-up time has arrived."
            )
            .setColor(
                android.graphics.Color.rgb(24, 27, 37)
            )
            .setCategory(Notification.CATEGORY_ALARM)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()


        notificationManager.notify(
            alarmId.hashCode(),
            notification
        )
    }
}
