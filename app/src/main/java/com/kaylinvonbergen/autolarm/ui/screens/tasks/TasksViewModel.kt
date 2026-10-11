
package com.kaylinvonbergen.autolarm.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.kaylinvonbergen.autolarm.data.repository.TaskRepository
import com.kaylinvonbergen.autolarm.domain.model.Task

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Manages task data and operations for the Tasks screen.
 */
class TasksViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    /**
     * Observable list of saved tasks.
     *
     * Automatically updates when Room data changes.
     */
    val tasks: StateFlow<List<Task>> =
        repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Create a new task.
     */
    fun createTask(task: Task) {
        viewModelScope.launch {
            repository.upsertTask(task)
        }
    }

    /**
     * Update an existing task.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.upsertTask(task)
        }
    }

    /**
     * Delete an existing task.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    /**
     * Retrieve a task by ID.
     */
    suspend fun getTaskById(id: String): Task? {
        return repository.getTaskById(id)
    }

    /**
     * Factory for constructing TasksViewModel
     * with its repository dependency.
     */
    companion object {

        fun factory(
            repository: TaskRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    require(
                        modelClass.isAssignableFrom(
                            TasksViewModel::class.java
                        )
                    ) {
                        "Unknown ViewModel class: ${modelClass.name}"
                    }

                    return TasksViewModel(repository) as T
                }
            }
        }
    }
}
