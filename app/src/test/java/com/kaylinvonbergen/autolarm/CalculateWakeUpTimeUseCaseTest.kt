
package com.kaylinvonbergen.autolarm.domain.usecase

import com.kaylinvonbergen.autolarm.domain.model.Alarm
import com.kaylinvonbergen.autolarm.domain.model.Task
import com.kaylinvonbergen.autolarm.domain.model.AdditionalAlarmConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class CalculateWakeUpTimeUseCaseTest {

    private val calculateWakeUpTime = CalculateWakeUpTimeUseCase()

    @Test
    fun calculatesWakeUpTimeCorrectly() {
        val shower = Task(
            name = "Shower",
            durationMinutes = 15
        )

        val breakfast = Task(
            name = "Breakfast",
            durationMinutes = 21
        )

        val alarm = Alarm(
            arrivalTime = LocalTime.of(9, 0),
            travelDurationMinutes = 24,
            bufferMinutes = 10,
            selectedTaskIds = listOf(shower.id, breakfast.id)
        )

        val result = calculateWakeUpTime(
            alarm = alarm,
            availableTasks = listOf(shower, breakfast)
        )

        // 15 + 21 + 24 + 10 = 70 minutes
        assertEquals(LocalTime.of(7, 50), result.wakeUpTime)
        assertEquals(36, result.totalTaskMinutes)
        assertEquals(46, result.totalPreparationMinutes)
        assertEquals(70, result.totalRequiredMinutes)
    }

    @Test
    fun generatesPreAndPostAlarmsCorrectly() {
        val alarm = Alarm(
            arrivalTime = LocalTime.of(8, 0),

            preAlarms = AdditionalAlarmConfig(
                enabled = true,
                count = 2,
                intervalMinutes = 5
            ),

            postAlarms = AdditionalAlarmConfig(
                enabled = true,
                count = 2,
                intervalMinutes = 5
            )
        )

        val result = calculateWakeUpTime(
            alarm = alarm,
            availableTasks = emptyList()
        )

        assertEquals(
            listOf(LocalTime.of(7, 50), LocalTime.of(7, 55)),
            result.preAlarmTimes
        )

        assertEquals(
            listOf(LocalTime.of(8, 5), LocalTime.of(8, 10)),
            result.postAlarmTimes
        )
    }

    @Test
    fun disabledAdditionalAlarmsProduceEmptyLists() {
        val alarm = Alarm(
            arrivalTime = LocalTime.of(9, 0)
        )

        val result = calculateWakeUpTime(
            alarm = alarm,
            availableTasks = emptyList()
        )

        assertTrue(result.preAlarmTimes.isEmpty())
        assertTrue(result.postAlarmTimes.isEmpty())
    }

    @Test
    fun correctlyHandlesMidnight() {
        val alarm = Alarm(
            arrivalTime = LocalTime.of(0, 30),
            travelDurationMinutes = 60
        )

        val result = calculateWakeUpTime(
            alarm = alarm,
            availableTasks = emptyList()
        )

        assertEquals(
            LocalTime.of(23, 30),
            result.wakeUpTime
        )
    }
}
