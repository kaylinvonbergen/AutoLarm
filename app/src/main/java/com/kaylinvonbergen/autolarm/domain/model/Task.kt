
package com.kaylinvonbergen.autolarm.domain.model

import java.util.UUID

/**
 * Represents a reusable task in the user's task library.
 *
 * Tasks can be selected for multiple alarms.
 *
 * @property id Unique identifier for the task.
 * @property name User-defined task name.
 * @property durationMinutes Estimated time to complete the task.
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val durationMinutes: Int
) {
    init {
        require(name.isNotBlank()) {
            "Task name cannot be blank."
        }

        require(durationMinutes > 0) {
            "Task duration must be greater than zero."
        }
    }
}
