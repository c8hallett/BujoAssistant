package com.hallett.bujoass.presentation.ui.view_task

import com.hallett.bujoass.domain.usecase.IObserveSingleTaskUseCase
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import com.hallett.bujoass.presentation.format.Formatter
import com.hallett.bujoass.presentation.model.PScopeInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ViewTaskDialogFragmentViewModel(
    private val observeSingleTaskUseCase: IObserveSingleTaskUseCase,
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
                it.status
            )
        }
    }
}