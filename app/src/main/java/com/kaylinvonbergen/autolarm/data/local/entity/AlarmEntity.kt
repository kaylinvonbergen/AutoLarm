
package com.kaylinvonbergen.autolarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaylinvonbergen.autolarm.domain.model.*
import org.json.JSONArray
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val id: String,
    val name: String,
    val enabled: Boolean,
    val arrivalDate: String,
    val arrivalTime: String,
    val departureLocation: String,
    val destinationLocation: String,
    val transportationMode: String,
    val travelTimeSource: String,
    val travelDurationMinutes: Int,
    val selectedTaskIds: String,
    val bufferMinutes: Int,
    val preEnabled: Boolean,
    val preCount: Int,
    val preInterval: Int,
    val postEnabled: Boolean,
    val postCount: Int,
    val postInterval: Int,
    val position: Int
)

fun Alarm.toEntity() = AlarmEntity(
    id = id,
    name = name,
    enabled = enabled,
    arrivalDate = arrivalDate.toString(),
    arrivalTime = arrivalTime.toString(),
    departureLocation = departureLocation,
    destinationLocation = destinationLocation,
    transportationMode = transportationMode.name,
    travelTimeSource = travelTimeSource.name,
    travelDurationMinutes = travelDurationMinutes,
    selectedTaskIds = JSONArray(selectedTaskIds).toString(),
    bufferMinutes = bufferMinutes,
    preEnabled = preAlarms.enabled,
    preCount = preAlarms.count,
    preInterval = preAlarms.intervalMinutes,
    postEnabled = postAlarms.enabled,
    postCount = postAlarms.count,
    postInterval = postAlarms.intervalMinutes,
    position = position
)

fun AlarmEntity.toDomain(): Alarm {
    val ids = JSONArray(selectedTaskIds)

    return Alarm(
        id = id,
        name = name,
        enabled = enabled,
        arrivalDate = LocalDate.parse(arrivalDate),
        arrivalTime = LocalTime.parse(arrivalTime),
        departureLocation = departureLocation,
        destinationLocation = destinationLocation,
        transportationMode = TransportationMode.valueOf(transportationMode),
        travelTimeSource = TravelTimeSource.valueOf(travelTimeSource),
        travelDurationMinutes = travelDurationMinutes,
        selectedTaskIds = (0 until ids.length()).map { ids.getString(it) },
        bufferMinutes = bufferMinutes,
        preAlarms = AdditionalAlarmConfig(
            preEnabled, preCount, preInterval
        ),
        postAlarms = AdditionalAlarmConfig(
            postEnabled, postCount, postInterval
        ),
        position = position
    )
}
