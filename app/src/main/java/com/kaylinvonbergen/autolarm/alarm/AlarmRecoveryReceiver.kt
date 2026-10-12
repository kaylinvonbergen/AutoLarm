
package com.kaylinvonbergen.autolarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import com.kaylinvonbergen.autolarm.data.local.AutoLarmDatabase
import com.kaylinvonbergen.autolarm.data.local.entity.toDomain
import com.kaylinvonbergen.autolarm.domain.usecase.CalculateWakeUpTimeUseCase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class AlarmRecoveryReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val action = intent.action

        if (
            action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_TIMEZONE_CHANGED &&
            action != Intent.ACTION_TIME_CHANGED
        ) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        ).launch {
            try {
                val database =
                    AutoLarmDatabase.getDatabase(appContext)

                val savedAlarms = database.alarmDao()
                    .observeAlarms()
                    .first()
                    .map { it.toDomain() }

                val savedTasks = database.taskDao()
                    .getAllTasks()
                    .first()
                    .map { it.toDomain() }

                val calculator =
                    CalculateWakeUpTimeUseCase()

                val existingTaskIds = savedTasks
                    .map { it.id }
                    .toSet()

                val desiredSchedules = savedAlarms
                    .filter { it.enabled }
                    .associate { alarm ->

                        val validAlarm = alarm.copy(
                            selectedTaskIds =
                                alarm.selectedTaskIds.filter {
                                    it in existingTaskIds
                                }
                        )

                        alarm.id to calculator(
                            validAlarm,
                            savedTasks
                        ).wakeUpTime
                    }

                AlarmScheduler(appContext)
                    .forceReconcile(desiredSchedules)

                Log.d(
                    "AutoLarm",
                    "Recovered ${desiredSchedules.size} alarms"
                )

            } catch (e: Exception) {
                Log.e(
                    "AutoLarm",
                    "Alarm recovery failed",
                    e
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
