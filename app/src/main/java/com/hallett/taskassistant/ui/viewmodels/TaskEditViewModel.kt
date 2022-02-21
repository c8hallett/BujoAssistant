package com.hallett.taskassistant.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hallett.database.ITaskRepository
import com.hallett.scopes.model.Scope
import com.hallett.domain.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@FlowPreview
class TaskEditViewModel(
    private val taskRepo: ITaskRepository
) : ViewModel() {

    private val taskFlow = MutableStateFlow(Task.DEFAULT_VALUE)

    fun getTaskName(taskId: Long): Flow<String> {
        viewModelScope.launch(Dispatchers.IO) {
            val newTaskToEmit = when (val task = taskRepo.getTask(taskId = taskId)) {
                null -> Task.DEFAULT_VALUE
                else -> with(task) {
                    Task(
                        id = id,
                        taskName = taskName,
                        scope = scope,
                        status = status
                    )
                }
            }
            taskFlow.emit(newTaskToEmit)
        }
        return taskFlow.map { it.taskName }
    }

    fun setTaskName(name: String) {
        viewModelScope.launch {
            taskFlow.emit(taskFlow.value.copy(taskName = name))
        }
    }

    fun setTaskScope(scope: Scope?) {
        viewModelScope.launch {
            taskFlow.emit(taskFlow.value.copy(scope = scope))
        }
    }

    fun onTaskSubmitted() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("ViewModel", "Submitting task: ${taskFlow.value}")
            if (taskFlow.value == Task.DEFAULT_VALUE) return@launch
            taskRepo.upsert(taskFlow.value)
        }
    }

    fun observeSelectedScope(): Flow<Scope?> = taskFlow.map { it.scope }

}