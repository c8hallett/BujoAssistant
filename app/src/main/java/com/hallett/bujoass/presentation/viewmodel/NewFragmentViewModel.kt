package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.domain.usecase.ISaveNewTaskUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewFragmentViewModel(
    private val saveNewTaskUseCase: ISaveNewTaskUseCase
): ViewModel() {

    private val selectedDateFlow = MutableStateFlow(Calendar.getInstance())
    private val selectedScopeFlow = MutableStateFlow(PScope.NONE)
    private val scopeOptionFlow: MutableStateFlow<List<Int>> by lazy {
        MutableStateFlow(PScope.values().map { it.displayName })
    }
    private val showExtraData = MutableStateFlow(false)

    fun selectDate(year: Int, month: Int, date: Int) {
        viewModelScope.launch {
            selectedDateFlow.emit(selectedDateFlow.value.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, date)
            })
        }
    }

    fun selectScope(scopeIndex: Int) {
        viewModelScope.launch {
            selectedScopeFlow.emit(PScope.values()[scopeIndex])
            showExtraData.emit(scopeIndex > PScope.NONE.ordinal)
        }
    }

    fun saveTask(taskName: String) {
        viewModelScope.launch {
            saveNewTaskUseCase.execute(taskName, PScopeInstance(selectedScopeFlow.value, selectedDateFlow.value.time))
        }
    }

    fun observeSelectedDate(): Flow<String> = selectedDateFlow.map { SimpleDateFormat("MMM dd, YYYY").format(it.time) }
    fun observeSelectedScopeIndex(): Flow<Int> = selectedScopeFlow.map { it.ordinal }
    fun observeScopeOptions(): Flow<List<Int>> = scopeOptionFlow
    fun observeShouldShowExtraData(): Flow<Boolean> = showExtraData

}