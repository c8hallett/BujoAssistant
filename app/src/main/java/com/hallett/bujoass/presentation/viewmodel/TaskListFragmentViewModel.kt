package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.usecase.IObserveTaskListFlowableUseCase
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
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

    private val selectedDateFlow = MutableStateFlow(Date())
    private val selectedScopeFlow = MutableStateFlow(PScope.NONE)
    private val scopeOptionFlow: MutableStateFlow<List<Int>> by lazy {
        MutableStateFlow(PScope.values().map { it.displayName })
    }
    private val scopeFlow = selectedScopeFlow.combine(selectedDateFlow){ type, date ->
        PScopeInstance(type, date)
    }

    fun selectDate(year: Int, month: Int, date: Int) {
        viewModelScope.launch {
            val newSelectedDate = Calendar.getInstance().apply {
                time = selectedDateFlow.value

                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, date)
            }.time
            selectedDateFlow.emit(newSelectedDate)
        }
    }

    fun selectScope(scopeIndex: Int) {
        viewModelScope.launch {
            selectedScopeFlow.emit(PScope.values()[scopeIndex])
        }
    }

    fun observeTaskList(): Flow<List<Task>> = observeTaskListFlowableUseCase.execute(scopeFlow)
    fun observeSelectedDate(): Flow<String> = selectedDateFlow.map{ SimpleDateFormat("MMM dd, YYYY").format(it) }
    fun observeSelectedScopeIndex(): Flow<Int> = selectedScopeFlow.map { it.ordinal }
    fun observeScopeOptions(): Flow<List<Int>> = scopeOptionFlow
}