package com.hallett.bujoass.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val selectedDateFlow = MutableStateFlow(formatUiDateString())

    fun updateSelectedTime(year: Int, month: Int, date: Int) {
        viewModelScope.launch {
            currentlySelectedDateTime = currentlySelectedDateTime.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, date)
            }
            selectedDateFlow.emit(formatUiDateString())
        }
    }

    fun observeSelectedDate(): Flow<String> = selectedDateFlow

    private fun formatUiDateString(): String = SimpleDateFormat("MMM dd, YYYY").format(currentlySelectedDateTime.time)

}