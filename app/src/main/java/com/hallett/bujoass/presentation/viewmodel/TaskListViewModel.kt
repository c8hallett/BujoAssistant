package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.hallett.bujoass.domain.usecase.IObserveTaskListFlowableUseCase
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class TaskListViewModel(
    private val observeTaskListFlowableUseCase: IObserveTaskListFlowableUseCase
): ViewModel() {

    private val selectedDateFlow = MutableStateFlow(Date())
    private val selectedScopeFlow = MutableStateFlow(PScope.NONE)
    private val scopeOptionFlow: MutableStateFlow<List<Int>> by lazy {
        MutableStateFlow(PScope.values().map { it.displayName })
    }
    private val scopeFlow = selectedScopeFlow.combine(selectedDateFlow){ type, date ->
        PScopeInstance(type, date)
    }

    fun observeTaskList(): Flow<List<Task>> = observeTaskListFlowableUseCase.execute(scopeFlow)

    fun observeSelectedDate(): Flow<String> = selectedDateFlow.map{ SimpleDateFormat("MMM dd, YYYY").format(it.time) }
    fun observeSelectedScopeIndex(): Flow<Int> = selectedScopeFlow.map { it.ordinal }
    fun observeScopeOptions(): Flow<List<Int>> = scopeOptionFlow
}