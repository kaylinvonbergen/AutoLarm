
package com.kaylinvonbergen.autolarm.domain.model

import java.time.LocalTime
import java.util.UUID

/**
 * Supported transportation methods.
 */
enum class TransportationMode {
    DRIVING,
    TRANSIT,
    WALKING,
    CYCLING
}

/**
 * Determines how travel duration is calculated.
 */
enum class TravelTimeSource {
    MANUAL,
    GOOGLE_MAPS
}

/**
 * Configuration for alarms that ring before or after
 * the calculated wake-up time.
 *
 * @property enabled Whether these additional alarms are active.
 * @property count Number of additional alarms.
 * @property intervalMinutes Minutes between alarms.
 */
data class AdditionalAlarmConfig(
    val enabled: Boolean = false,
    val count: Int = 0,
    val intervalMinutes: Int = 5
) {
    init {
        require(count >= 0) {
            "Alarm count cannot be negative."
        }

        require(intervalMinutes > 0) {
            "Alarm interval must be greater than zero."
        }
    }
}

/**
 * Represents an AutoLarm alarm configuration.
 *
 * The actual wake-up time is calculated from the arrival
 * time, selected tasks, travel duration, and buffer time.
 *
 * @property id Unique identifier for the alarm.
 * @property name Optional user-defined alarm name.
 * @property enabled Whether the alarm is active.
 * @property arrivalTime Desired arrival time.
 * @property departureLocation Starting location.
 * @property destinationLocation Destination.
 * @property transportationMode Selected transportation method.
 * @property travelTimeSource Manual or Google Maps estimate.
 * @property travelDurationMinutes Estimated travel time.
 * @property selectedTaskIds IDs of tasks included in this alarm.
 * @property bufferMinutes Additional preparation time.
 * @property preAlarms Configuration for alarms before wake-up.
 * @property postAlarms Configuration for alarms after wake-up.
 */
data class Alarm(
    val id: String = UUID.randomUUID().toString(),

    val name: String = "",
    val enabled: Boolean = true,

    // Arrival information
    val arrivalTime: LocalTime,
    val departureLocation: String = "",
    val destinationLocation: String = "",

    // Transportation
    val transportationMode: TransportationMode =
        TransportationMode.DRIVING,

    val travelTimeSource: TravelTimeSource =
        TravelTimeSource.MANUAL,

    val travelDurationMinutes: Int = 0,

    // Morning routine
    val selectedTaskIds: List<String> = emptyList(),
    val bufferMinutes: Int = 0,

    // Additional alarms
    val preAlarms: AdditionalAlarmConfig =
        AdditionalAlarmConfig(),

    val postAlarms: AdditionalAlarmConfig =
        AdditionalAlarmConfig()
) {
    init {
        require(travelDurationMinutes >= 0) {
            "Travel duration cannot be negative."
        }

        require(bufferMinutes >= 0) {
            "Buffer time cannot be negative."
        }

        require(selectedTaskIds.distinct().size == selectedTaskIds.size) {
            "Selected tasks cannot contain duplicate IDs."
        }
    }
}
