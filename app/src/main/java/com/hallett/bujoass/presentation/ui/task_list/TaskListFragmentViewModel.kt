package com.hallett.bujoass.presentation.ui.task_list

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.usecase.observe_task.IObserveTaskListFlowableUseCase
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragmentViewModel(
    private val observeTaskListFlowableUseCase: IObserveTaskListFlowableUseCase
): BujoAssViewModel() {

    private var selectedScopeFlow = MutableStateFlow(PScopeInstance(PScope.NONE, Date()))
    fun onNewScopeSelected(pScopeInstance: PScopeInstance) {
        viewModelScope.launch {
            selectedScopeFlow.emit(pScopeInstance)
        }
    }

    fun observeTaskList(): Flow<List<Task>> = observeTaskListFlowableUseCase.execute(selectedScopeFlow)
    fun observeNewScopeSelected(): Flow<PScopeInstance> = selectedScopeFlow

}