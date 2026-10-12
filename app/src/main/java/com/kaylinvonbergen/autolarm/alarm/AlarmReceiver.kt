
package com.kaylinvonbergen.autolarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

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

        if (!scheduler.handleTriggeredAlarm(alarmId, trigger)) {
            Log.d("AutoLarm", "Ignored obsolete alarm")
            return
        }

        Log.d("AutoLarm", "Alarm fired: $alarmId")

        val serviceIntent = Intent(
            context,
            AlarmRingingService::class.java
        ).apply {
            action = AlarmRingingService.ACTION_START
            putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarmId)
        }

        try {
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e(
                "AutoLarm",
                "Unable to start ringing service",
                e
            )
        }
    }
}
