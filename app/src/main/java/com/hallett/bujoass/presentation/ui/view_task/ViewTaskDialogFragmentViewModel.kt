package com.hallett.bujoass.presentation.ui.view_task

import android.app.Presentation
import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.domain.usecase.modify_task.IDeferTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IDeleteTaskUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IModifyTaskStatusUseCase
import com.hallett.bujoass.domain.usecase.modify_task.IRescheduleTaskUseCase
import com.hallett.bujoass.domain.usecase.observe_task.IObserveSingleTaskUseCase
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import com.hallett.bujoass.presentation.format.Formatter
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.PresentationResult
import kotlinx.coroutines.flow.*
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

    enum class Request {
        OBSERVE,
        UPDATE_STATUS,
        RESCHEDULE,
        DELETE,
    }

    sealed class PresentationException(val request: Request, message: String? = null, cause: Throwable? = null): Exception(message, cause) {
        class TaskNoLongerExists(request: Request): PresentationException(request, "Task no longer exists")
        object TaskIsNotScheduled: PresentationException(Request.RESCHEDULE, "Task is not scheduled, could not defer")

        class UnknownFailure(request: Request, cause: Throwable, message: String? = null): PresentationException(request, message, cause)
    }

    private var presentationFlow = MutableSharedFlow<PresentationResult<ViewableTask>>()

    private var taskId: Long = -1L
    fun observeTask(taskId: Long): Flow<PresentationResult<ViewableTask>> {
        this.taskId = taskId
        viewModelScope.launch {
            observeSingleTaskUseCase.execute(taskId)
                .catch {
                    presentationFlow.emitFailure(PresentationException.TaskNoLongerExists(Request.OBSERVE))
                }
                .collect {
                    val task = ViewableTask(
                        it.taskName,
                        it.scope.type,
                        scopeFormatter.format(it.scope),
                        it.status,
                        it.isCurrentScope,
                    )
                    presentationFlow.emitSuccess(task)
                }
        }
        return presentationFlow
    }

    fun updateStatus(status: TaskStatus) {
        viewModelScope.launch {
            try{
                modifyTaskStatusUseCase.execute(taskId, status)
            } catch(t: Throwable) {
                presentationFlow.emitFailure(PresentationException.UnknownFailure(Request.UPDATE_STATUS, t, "Couldn't modify task status"))
            }
        }
    }

    fun deferTask() {
        viewModelScope.launch {
            try {
                deferTaskUseCase.execute(taskId)
            } catch (t: Throwable) {
                when(t){
                    is IllegalArgumentException -> presentationFlow.emitFailure(PresentationException.TaskNoLongerExists(Request.RESCHEDULE))
                    is IllegalStateException -> presentationFlow.emitFailure(PresentationException.TaskIsNotScheduled)
                    else -> presentationFlow.emitFailure(PresentationException.UnknownFailure(Request.RESCHEDULE, t))
                }
            }
        }
    }

    fun rescheduleTask(scope: PScopeInstance) {
        viewModelScope.launch {
            try{
                rescheduleTaskUseCase.execute(taskId, scope)
            } catch(t: Throwable) {
                presentationFlow.emitFailure(PresentationException.UnknownFailure(Request.RESCHEDULE, t))
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            try {
                deleteTaskUseCase.execute(taskId)
            } catch(t: Throwable){
                presentationFlow.emitFailure(PresentationException.UnknownFailure(Request.DELETE, t))
            }
        }
    }

    fun moveTaskToCurrentScope(scope: PScope) {
        viewModelScope.launch {
            val pScope = PScopeInstance(scope, Date())
            try{
                rescheduleTaskUseCase.execute(taskId, pScope)
            } catch(t: Throwable) {
                presentationFlow.emitFailure(PresentationException.UnknownFailure(Request.RESCHEDULE, t))
            }
        }
    }
}