
package com.kaylinvonbergen.autolarm.ui.screens.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.kaylinvonbergen.autolarm.data.repository.AlarmRepository
import com.kaylinvonbergen.autolarm.domain.model.Alarm
import com.kaylinvonbergen.autolarm.domain.model.Task
import com.kaylinvonbergen.autolarm.domain.usecase.CalculateWakeUpTimeUseCase

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlarmsViewModel(
    private val repository: AlarmRepository
) : ViewModel() {

    val alarms: StateFlow<List<Alarm>> =
        repository.alarms.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private val calculator = CalculateWakeUpTimeUseCase()

    fun calculate(
        alarm: Alarm,
        tasks: List<Task>
    ): CalculateWakeUpTimeUseCase.Result {
        // Ignore task IDs that no longer exist.
        val existingIds = tasks.map { it.id }.toSet()
        val validAlarm = alarm.copy(
            selectedTaskIds = alarm.selectedTaskIds.filter {
                it in existingIds
            }
        )
        return calculator(validAlarm, tasks)
    }

    fun createAlarm() {
        viewModelScope.launch {
            // Determine position from persisted alarms.
            val existing = repository.alarms.first()

            if (existing.size >= 5) return@launch

            val nextPosition =
                (existing.maxOfOrNull { it.position } ?: -1) + 1

            repository.create(
                Alarm(
                    name = "Morning Alarm",
                    position = nextPosition
                )
            )
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.update(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.delete(alarm.id)
        }
    }

    companion object {
        fun factory(
            repository: AlarmRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    require(
                        modelClass.isAssignableFrom(
                            AlarmsViewModel::class.java
                        )
                    )
                    return AlarmsViewModel(repository) as T
                }
            }
        }
    }
}
