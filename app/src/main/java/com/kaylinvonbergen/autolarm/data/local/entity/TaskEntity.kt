
package com.kaylinvonbergen.autolarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaylinvonbergen.autolarm.domain.model.Task

/**
 * Database representation of a Task.
 *
 * Each row represents one reusable task.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val durationMinutes: Int
)

/**
 * Convert a database entity into a domain model.
 */
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        name = name,
        durationMinutes = durationMinutes
    )
}

/**
 * Convert a domain model into a database entity.
 */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        name = name,
        durationMinutes = durationMinutes
    )
}
