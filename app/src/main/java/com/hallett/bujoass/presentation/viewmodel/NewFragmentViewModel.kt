package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.domain.usecase.ISaveNewTaskUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewFragmentViewModel(
    private val saveNewTaskUseCase: ISaveNewTaskUseCase
): ViewModel() {

    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedScope: PScope = PScope.NONE

    private val selectedDateFlow = MutableStateFlow(formatUiDateString())
    private val selectedScopeFlow = MutableStateFlow(PScope.NONE.ordinal)
    private val scopeOptionFlow: MutableStateFlow<List<Int>> by lazy {
        MutableStateFlow(PScope.values().map { it.displayName })
    }
    private val showExtraData = MutableStateFlow(false)

    fun selectDate(year: Int, month: Int, date: Int) {
        viewModelScope.launch {
            selectedDate = selectedDate.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, date)
            }
            selectedDateFlow.emit(formatUiDateString())
        }
    }

    fun selectScope(scopeIndex: Int) {
        selectedScope = PScope.values()[scopeIndex]
        viewModelScope.launch {
            showExtraData.emit(scopeIndex > PScope.NONE.ordinal)
        }
    }

    fun saveTask(taskName: String) {
        viewModelScope.launch {
            saveNewTaskUseCase.execute(taskName, PScopeInstance(selectedScope, selectedDate.time))
        }
    }

    fun observeSelectedDate(): Flow<String> = selectedDateFlow
    fun observeSelectedScopeIndex(): Flow<Int> = selectedScopeFlow
    fun observeScopeOptions(): Flow<List<Int>> = scopeOptionFlow
    fun observeShouldShowDate(): Flow<Boolean> = showExtraData


    private fun formatUiDateString(): String = SimpleDateFormat("MMM dd, YYYY").format(selectedDate.time)

}