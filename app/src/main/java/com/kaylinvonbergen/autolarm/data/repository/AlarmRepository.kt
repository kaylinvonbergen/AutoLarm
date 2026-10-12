
package com.kaylinvonbergen.autolarm.data.repository

import com.kaylinvonbergen.autolarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    val alarms: Flow<List<Alarm>>

    suspend fun create(alarm: Alarm): Boolean
    suspend fun update(alarm: Alarm)
    suspend fun delete(id: String)
}
