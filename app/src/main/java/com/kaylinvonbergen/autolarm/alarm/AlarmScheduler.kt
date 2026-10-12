
package com.kaylinvonbergen.autolarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(context: Context) {

    private val appContext = context.applicationContext

    private val manager = appContext.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager

    private val prefs = appContext.getSharedPreferences(
        "autolarm_alarm_schedule",
        Context.MODE_PRIVATE
    )

    companion object {
        const val ACTION_FIRE =
            "com.kaylinvonbergen.autolarm.ACTION_FIRE_ALARM"

        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_TRIGGER = "trigger_time"

        private const val KEY_IDS = "scheduled_ids"

        private fun minutesKey(id: String) = "minutes_$id"
        private fun triggerKey(id: String) = "trigger_$id"
    }

    fun canScheduleExactAlarms(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                manager.canScheduleExactAlarms()
    }

    /**
     * Match Android's scheduled alarms to the currently
     * enabled AutoLarm configurations.
     *
     * Each entry maps alarm ID -> local wake-up time.
     */
    fun reconcile(desired: Map<String, LocalTime>) {
        val oldIds = prefs.getStringSet(
            KEY_IDS,
            emptySet()
        )?.toSet().orEmpty()

        val newIds = desired.keys

        // Cancel alarms that were deleted or disabled.
        (oldIds - newIds).forEach { id ->
            cancel(id)
        }

        prefs.edit()
            .putStringSet(KEY_IDS, newIds.toSet())
            .apply()

        // Schedule enabled alarms.
        desired.forEach { (id, time) ->
            val minutes = time.hour * 60 + time.minute
            val previousMinutes = prefs.getInt(
                minutesKey(id),
                -1
            )

            val previousTrigger = prefs.getLong(
                triggerKey(id),
                -1L
            )

            val alreadyScheduled =
                previousMinutes == minutes &&
                        previousTrigger > System.currentTimeMillis()

            if (!canScheduleExactAlarms()) {
                // The system may have removed scheduled alarms
                // when permission was revoked.
                prefs.edit()
                    .putInt(minutesKey(id), minutes)
                    .remove(triggerKey(id))
                    .apply()

                return@forEach
            }

            if (!alreadyScheduled) {
                scheduleNext(id, time)
            }
        }
    }

    /**
     * Schedule the next occurrence of the specified
     * local time. Does not use the saved arrivalDate.
     */
    fun scheduleNext(
        id: String,
        time: LocalTime
    ): Boolean {
        if (!canScheduleExactAlarms()) {
            return false
        }

        val zone = ZoneId.systemDefault()
        val now = java.time.ZonedDateTime.now(zone)

        var date = LocalDate.now(zone)
        var next = date.atTime(time).atZone(zone)

        if (!next.isAfter(now)) {
            date = date.plusDays(1)
            next = date.atTime(time).atZone(zone)
        }

        val triggerMillis = next.toInstant().toEpochMilli()
        val minutes = time.hour * 60 + time.minute

        val pendingIntent = pendingIntent(
            id = id,
            triggerMillis = triggerMillis
        )

        return try {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )

            prefs.edit()
                .putInt(minutesKey(id), minutes)
                .putLong(triggerKey(id), triggerMillis)
                .apply()

            Log.d(
                "AutoLarm",
                "Scheduled $id for $next"
            )

            true
        } catch (e: SecurityException) {
            Log.e(
                "AutoLarm",
                "Exact alarm permission unavailable",
                e
            )

            prefs.edit()
                .remove(triggerKey(id))
                .apply()

            false
        }
    }

    /**
     * Called by AlarmReceiver after an alarm fires.
     *
     * Only re-arms if this is still the current
     * scheduled occurrence of an enabled alarm.
     */
    fun handleTriggeredAlarm(
        id: String,
        receivedTrigger: Long
    ): Boolean {
        val enabledIds = prefs.getStringSet(
            KEY_IDS,
            emptySet()
        ).orEmpty()

        if (id !in enabledIds) return false

        val expectedTrigger = prefs.getLong(
            triggerKey(id),
            -1L
        )

        if (receivedTrigger != expectedTrigger) {
            return false
        }

        val minutes = prefs.getInt(
            minutesKey(id),
            -1
        )

        if (minutes !in 0..1439) return false

        // Clear the fired occurrence before re-arming.
        prefs.edit()
            .remove(triggerKey(id))
            .commit()

        val time = LocalTime.of(
            minutes / 60,
            minutes % 60
        )

        scheduleNext(id, time)

        return true
    }

    fun cancel(id: String) {
        val intent = baseIntent(id)

        val existing = PendingIntent.getBroadcast(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_NO_CREATE or
                    PendingIntent.FLAG_IMMUTABLE
        )

        if (existing != null) {
            manager.cancel(existing)
            existing.cancel()
        }

        prefs.edit()
            .remove(minutesKey(id))
            .remove(triggerKey(id))
            .apply()

        Log.d("AutoLarm", "Cancelled alarm $id")
    }

    private fun pendingIntent(
        id: String,
        triggerMillis: Long
    ): PendingIntent {
        val intent = baseIntent(id).apply {
            putExtra(EXTRA_ALARM_ID, id)
            putExtra(EXTRA_TRIGGER, triggerMillis)
        }

        return PendingIntent.getBroadcast(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun baseIntent(id: String): Intent {
        return Intent(
            appContext,
            AlarmReceiver::class.java
        ).apply {
            action = ACTION_FIRE

            // Unique data URI keeps each alarm independent.
            data = Uri.parse(
                "autolarm://alarm/${Uri.encode(id)}"
            )
        }
    }


    /**
     * Force every enabled alarm to be rescheduled.
     *
     * Use after a device restart, time-zone change,
     * or manual system clock change.
     */
    fun forceReconcile(desired: Map<String, LocalTime>) {
        val oldIds = prefs.getStringSet(
            KEY_IDS,
            emptySet()
        )?.toSet().orEmpty()

        // Invalidate cached trigger times for every known ID.
        (oldIds + desired.keys).forEach { id ->
            prefs.edit()
                .remove(triggerKey(id))
                .apply()
        }

        reconcile(desired)
    }

}
