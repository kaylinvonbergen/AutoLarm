
package com.kaylinvonbergen.autolarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaylinvonbergen.autolarm.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /**
     * Observe all tasks.
     * Emits an updated list whenever the table changes.
     */
    @Query("SELECT * FROM tasks ORDER BY name COLLATE NOCASE ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * Retrieve an individual task.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?

    /**
     * Insert or replace a task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    /**
     * Delete a task using its unique ID.
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    /**
     * Delete all tasks.
     * Useful for tests or a future reset feature.
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
