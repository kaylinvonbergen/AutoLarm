
package com.kaylinvonbergen.autolarm.domain.usecase

import com.kaylinvonbergen.autolarm.domain.model.Alarm
import com.kaylinvonbergen.autolarm.domain.model.Task
import com.kaylinvonbergen.autolarm.domain.model.AdditionalAlarmConfig
import java.time.LocalTime

/**
 * Calculates the wake-up time and associated alarm times
 * based on an arrival time, commute, tasks, and buffer.
 *
 * This class contains no Android-specific dependencies.
 */
class CalculateWakeUpTimeUseCase {

    /**
     * Result of an alarm time calculation.
     *
     * @property wakeUpTime Main get-up alarm time.
     * @property preAlarmTimes Alarms before the main alarm.
     * @property postAlarmTimes Alarms after the main alarm.
     * @property totalTaskMinutes Combined duration of selected tasks.
     * @property totalPreparationMinutes Tasks + buffer.
     * @property totalRequiredMinutes Tasks + buffer + travel.
     */
    data class Result(
        val wakeUpTime: LocalTime,
        val preAlarmTimes: List<LocalTime>,
        val postAlarmTimes: List<LocalTime>,
        val totalTaskMinutes: Int,
        val totalPreparationMinutes: Int,
        val totalRequiredMinutes: Int
    )

    /**
     * Calculates all alarm times.
     *
     * @param alarm Alarm configuration.
     * @param availableTasks All tasks in the user's task library.
     * @return Calculated wake-up and additional alarm times.
     */
    operator fun invoke(
        alarm: Alarm,
        availableTasks: List<Task>
    ): Result {

        // Look up tasks selected for this alarm.
        val tasksById = availableTasks.associateBy { it.id }

        // Ensure that every selected task exists.
        val selectedTasks = alarm.selectedTaskIds.map { taskId ->
            tasksById[taskId]
                ?: throw IllegalArgumentException(
                    "Selected task not found: $taskId"
                )
        }

        // Calculate combined task duration.
        val totalTaskMinutes = selectedTasks.sumOf {
            it.durationMinutes
        }

        // Tasks + additional buffer.
        val totalPreparationMinutes =
            totalTaskMinutes + alarm.bufferMinutes

        // Total time needed before arrival.
        val totalRequiredMinutes =
            totalPreparationMinutes + alarm.travelDurationMinutes

        // Work backward from arrival time.
        val wakeUpTime = alarm.arrivalTime.minusMinutes(
            totalRequiredMinutes.toLong()
        )

        // Generate pre-alarm times.
        val preAlarmTimes = generatePreAlarms(
            wakeUpTime,
            alarm.preAlarms
        )

        // Generate post-alarm times.
        val postAlarmTimes = generatePostAlarms(
            wakeUpTime,
            alarm.postAlarms
        )

        return Result(
            wakeUpTime = wakeUpTime,
            preAlarmTimes = preAlarmTimes,
            postAlarmTimes = postAlarmTimes,
            totalTaskMinutes = totalTaskMinutes,
            totalPreparationMinutes = totalPreparationMinutes,
            totalRequiredMinutes = totalRequiredMinutes
        )
    }

    /**
     * Generates alarms before the main wake-up time.
     *
     * Example:
     * Wake-up: 7:30 AM
     * Count: 2
     * Interval: 5 minutes
     *
     * Result: [7:20 AM, 7:25 AM]
     */
    private fun generatePreAlarms(
        wakeUpTime: LocalTime,
        config: AdditionalAlarmConfig
    ): List<LocalTime> {

        if (!config.enabled || config.count == 0) {
            return emptyList()
        }

        return (config.count downTo 1).map { index ->
            wakeUpTime.minusMinutes(
                index.toLong() * config.intervalMinutes
            )
        }
    }

    /**
     * Generates alarms after the main wake-up time.
     *
     * Example:
     * Wake-up: 7:30 AM
     * Count: 2
     * Interval: 5 minutes
     *
     * Result: [7:35 AM, 7:40 AM]
     */
    private fun generatePostAlarms(
        wakeUpTime: LocalTime,
        config: AdditionalAlarmConfig
    ): List<LocalTime> {

        if (!config.enabled || config.count == 0) {
            return emptyList()
        }

        return (1..config.count).map { index ->
            wakeUpTime.plusMinutes(
                index.toLong() * config.intervalMinutes
            )
        }
    }
}
