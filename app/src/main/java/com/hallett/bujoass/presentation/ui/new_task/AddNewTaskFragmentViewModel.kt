package com.hallett.bujoass.presentation.ui.new_task

import androidx.lifecycle.viewModelScope
import com.hallett.bujoass.domain.usecase.modify_task.ISaveNewTaskUseCase
import com.hallett.bujoass.presentation.model.PresentationResult
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import com.hallett.scopes.Scope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddNewTaskFragmentViewModel(
    private val saveNewTaskUseCase: ISaveNewTaskUseCase
): BujoAssViewModel() {

    private var selectedScopeFlow = MutableStateFlow<Scope?>(null)
    private val newTaskSavedFlow = MutableStateFlow<PresentationResult<Unit>>(PresentationResult.Loading)

    fun saveTask(taskName: String) {
        viewModelScope.launch {
            newTaskSavedFlow.emitResult {
                saveNewTaskUseCase.execute(taskName, selectedScopeFlow.value)
            }
        }
    }
    fun onNewScopeSelected(scope: Scope?) {
        viewModelScope.launch {
            selectedScopeFlow.emit(scope)
        }
    }

    fun observeNewTaskSaved(): Flow<PresentationResult<Unit>> = newTaskSavedFlow
    fun observeNewScopeSelected(): Flow<Scope?> = selectedScopeFlow

}