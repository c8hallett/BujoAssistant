package com.hallett.bujoass.presentation.ui.view_task

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.domain.usecase.modify_task.IDeferTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IDeleteTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IModifyTaskStatusUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IRescheduleTaskUseCase
import com.hallett.bujoass.domain.usecase.observe_task.IObserveSingleTaskUseCase
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import com.hallett.bujoass.presentation.format.Formatter
import com.hallett.bujoass.presentation.model.PScopeInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class ViewTaskDialogFragmentViewModel(
    private val observeSingleTaskUseCase: IObserveSingleTaskUseCase,
    private val modifyTaskStatusUseCase: IModifyTaskStatusUseCase,
    private val rescheduleTaskUseCase: IRescheduleTaskUseCase,
    private val deferTaskUseCase: IDeferTaskUseCase,
    private val deleteTaskUseCase: IDeleteTaskUseCase,
    private val scopeFormatter: Formatter<PScopeInstance>,
): BujoAssViewModel() {

    private var taskId: Long = -1L
    fun observeTask(taskId: Long): Flow<ViewableTask> {
        this.taskId = taskId
        return observeSingleTaskUseCase.execute(taskId).map {
            ViewableTask(
                it.taskName,
                it.scope.type,
                scopeFormatter.format(it.scope),
                it.status,
                it.isCurrentScope,
            )
        }
    }

    fun updateStatus(status: TaskStatus) {
        viewModelScope.launch {
            modifyTaskStatusUseCase.execute(taskId, status)
        }
    }

    fun deferTask() {
        viewModelScope.launch {
            deferTaskUseCase.execute(taskId)
        }
    }

    fun rescheduleTask(scope: PScopeInstance) {
        viewModelScope.launch {
            rescheduleTaskUseCase.execute(taskId, scope)
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            deferTaskUseCase.execute(taskId)
        }
    }
}