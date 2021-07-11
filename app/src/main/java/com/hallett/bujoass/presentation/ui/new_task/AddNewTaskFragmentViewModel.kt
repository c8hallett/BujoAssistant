package com.hallett.bujoass.presentation.ui.new_task

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.domain.usecase.modify_task.ISaveNewTaskUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.PresentationResult
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class AddNewTaskFragmentViewModel(
    private val saveNewTaskUseCase: ISaveNewTaskUseCase
): BujoAssViewModel() {

    private var selectedScopeFlow = MutableStateFlow(PScopeInstance(PScope.NONE, Date()))
    private val newTaskSavedFlow = MutableStateFlow<PresentationResult<Unit>>(PresentationResult.Loading)

    fun saveTask(taskName: String) {
        viewModelScope.launch {
            newTaskSavedFlow.emitResult {
                saveNewTaskUseCase.execute(taskName, selectedScopeFlow.value)
            }
        }
    }
    fun onNewScopeSelected(pScopeInstance: PScopeInstance) {
        viewModelScope.launch {
            selectedScopeFlow.emit(pScopeInstance)
        }
    }

    fun observeNewTaskSaved(): Flow<PresentationResult<Unit>> = newTaskSavedFlow
    fun observeNewScopeSelected(): Flow<PScopeInstance> = selectedScopeFlow

}