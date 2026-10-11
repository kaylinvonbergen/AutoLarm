
package com.kaylinvonbergen.autolarm.data.repository

import com.kaylinvonbergen.autolarm.data.local.dao.TaskDao
import com.kaylinvonbergen.autolarm.data.local.entity.toDomain
import com.kaylinvonbergen.autolarm.data.local.entity.toEntity
import com.kaylinvonbergen.autolarm.domain.model.Task

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-based implementation of TaskRepository.
 */
class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override val allTasks: Flow<List<Task>> =
        taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.id)
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }
}
