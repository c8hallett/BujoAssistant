package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.presentation.PresentationScope
import com.hallett.bujoass.usecase.ISaveNewTaskUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewFragmentViewModel(
    private val saveNewTaskUseCase: ISaveNewTaskUseCase
): ViewModel() {

    private var currentlySelectedDateTime: Calendar = Calendar.getInstance()
    private var selectedScope: PresentationScope? = null

    private val selectedDateFlow = MutableStateFlow(formatUiDateString())
    private val selectedScopeFlow = MutableStateFlow(PresentationScope.NONE.ordinal)
    private val scopeOptionFlow: MutableStateFlow<List<Int>> by lazy {
        MutableStateFlow(PresentationScope.values().map { it.displayName })
    }
    private val showExtraData = MutableStateFlow(false)

    fun selectDate(year: Int, month: Int, date: Int) {
        viewModelScope.launch {
            currentlySelectedDateTime = currentlySelectedDateTime.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, date)
            }
            selectedDateFlow.emit(formatUiDateString())
        }
    }

    fun selectScope(scopeIndex: Int) {
        selectedScope = PresentationScope.values()[scopeIndex]
        viewModelScope.launch {
            showExtraData.emit(scopeIndex > PresentationScope.NONE.ordinal)
        }
    }


    fun observeSelectedDate(): Flow<String> = selectedDateFlow
    fun observeSelectedScopeIndex(): Flow<Int> = selectedScopeFlow
    fun observeScopeOptions(): Flow<List<Int>> = scopeOptionFlow
    fun observeShouldShowDate(): Flow<Boolean> = showExtraData


    private fun formatUiDateString(): String = SimpleDateFormat("MMM dd, YYYY").format(currentlySelectedDateTime.time)

}