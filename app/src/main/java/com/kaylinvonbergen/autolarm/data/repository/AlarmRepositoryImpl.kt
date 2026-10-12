
package com.kaylinvonbergen.autolarm.data.repository

import androidx.room.withTransaction
import com.kaylinvonbergen.autolarm.data.local.AutoLarmDatabase
import com.kaylinvonbergen.autolarm.data.local.entity.toDomain
import com.kaylinvonbergen.autolarm.data.local.entity.toEntity
import com.kaylinvonbergen.autolarm.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl(
    private val database: AutoLarmDatabase
) : AlarmRepository {

    private val dao = database.alarmDao()

    override val alarms: Flow<List<Alarm>> =
        dao.observeAlarms().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun create(alarm: Alarm): Boolean {
        return database.withTransaction {
            if (dao.count() >= 5) {
                false
            } else {
                dao.upsert(alarm.toEntity())
                true
            }
        }
    }

    override suspend fun update(alarm: Alarm) {
        dao.upsert(alarm.toEntity())
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
