
package com.kaylinvonbergen.autolarm.data.repository

import com.kaylinvonbergen.autolarm.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Contract for task data operations.
 *
 * The ViewModel depends on this interface rather than
 * interacting directly with the Room database.
 */
interface TaskRepository {

    val allTasks: Flow<List<Task>>

    suspend fun getTaskById(id: String): Task?

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun deleteAllTasks()
}
