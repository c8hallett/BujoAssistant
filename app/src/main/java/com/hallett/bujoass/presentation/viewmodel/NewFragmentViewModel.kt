package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.domain.usecase.ISaveNewTaskUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.PresentationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class NewFragmentViewModel(
    private val saveNewTaskUseCase: ISaveNewTaskUseCase
): BujoAssViewModel() {

    private val selectedDateFlow = MutableStateFlow(Date())
    private val selectedScopeFlow = MutableStateFlow(PScope.NONE)
    private val scopeOptionFlow: MutableStateFlow<List<Int>> by lazy {
        MutableStateFlow(PScope.values().map { it.displayName })
    }
    private val showExtraData = MutableStateFlow(false)
    private val newTaskSavedFlow = MutableStateFlow<PresentationResult<Unit>>(PresentationResult.Loading)

    fun selectDate(year: Int, month: Int, date: Int) {
        viewModelScope.launch {
            val newSelectedDate = Calendar.getInstance().apply {
                time = selectedDateFlow.value

                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, date)
            }.time

            Timber.i("Date to be emitted: $newSelectedDate")
            selectedDateFlow.emit(newSelectedDate)
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
            newTaskSavedFlow.emitResult {
                saveNewTaskUseCase.execute(taskName, PScopeInstance(selectedScopeFlow.value, selectedDateFlow.value))
            }
        }
    }

    fun observeSelectedDate(): Flow<String> = selectedDateFlow.map { SimpleDateFormat("MMM dd, YYYY").format(it) }
    fun observeSelectedScopeIndex(): Flow<Int> = selectedScopeFlow.map { it.ordinal }
    fun observeScopeOptions(): Flow<List<Int>> = scopeOptionFlow
    fun observeShouldShowExtraData(): Flow<Boolean> = showExtraData
    fun observeNewTaskSaved(): Flow<PresentationResult<Unit>> = newTaskSavedFlow.also { it.value = PresentationResult.Loading }

}