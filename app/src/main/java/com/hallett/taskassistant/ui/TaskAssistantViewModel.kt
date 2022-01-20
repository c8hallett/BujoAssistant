package com.hallett.taskassistant.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.database.task.TaskDao
import com.hallett.taskassistant.database.task.TaskEntity
import com.hallett.taskassistant.di.PagerParams
import com.hallett.taskassistant.domain.Task
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@FlowPreview
class TaskAssistantViewModel(
    private val taskDao: TaskDao,
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>
): ViewModel() {

    private companion object{
        val DEFAULT_SCOPE = ScopeType.DAY
    }

    private val taskFlow = MutableStateFlow(Task.DEFAULT_VALUE)
    private val scopeTypeSelected = MutableStateFlow(DEFAULT_SCOPE)


    fun getTaskName(taskId: Long = -1L): Flow<String> {
        viewModelScope.launch(Dispatchers.IO) {
            val savedTaskEntity = taskDao.getTask(taskId = taskId)
            if(savedTaskEntity != null){
                val mappedTask = with(savedTaskEntity){
                    Task(
                        id = id,
                        taskName = taskName,
                        scope = scope,
                        status = status
                    )
                }
                taskFlow.emit(mappedTask)
                scopeTypeSelected.emit(mappedTask.getScopeTypeOrDefault())
            }
        }
        return taskFlow.map { it.taskName }
    }

    fun setTaskName(name: String) {
        viewModelScope.launch {
            taskFlow.emit(taskFlow.value.copy(taskName = name))
        }
    }

    fun setTaskScope(scope: Scope?) {
        viewModelScope.launch {
            taskFlow.emit(taskFlow.value.copy(scope = scope))
        }
    }

    fun onTaskSubmitted() {
        viewModelScope.launch(Dispatchers.IO) {
            if(taskFlow.value == Task.DEFAULT_VALUE) return@launch
            taskDao.upsert(with(taskFlow.value){
                TaskEntity(
                    id = id,
                    taskName = taskName,
                    scope = scope,
                    status = status,
                    updatedAt = Date()
                )
            })
        }
    }

    fun onNewScopeTypeSelected(scopeType: ScopeType) {
        scopeTypeSelected.value = scopeType
    }

    fun observeScopeSelectorList(): Flow<PagingData<Scope>> = scopeTypeSelected.flatMapLatest { scopeType ->
        Log.i("TaskAssistantViewModel", "new scope type selected: $scopeType")
        generatePager(PagingConfig(pageSize = 10).toPagerParams(scopeType))
            .flow
            .flowOn(Dispatchers.Default)
    }

    fun observeScopeType(): Flow<ScopeType> = scopeTypeSelected

    fun observeSelectedScope(): Flow<Scope?> = taskFlow.map { it.scope }

    private fun Task.getScopeTypeOrDefault(): ScopeType {
        return scope?.type ?: DEFAULT_SCOPE
    }

    private fun PagingConfig.toPagerParams(scopeType: ScopeType): PagerParams {
        return PagerParams(this, scopeType)
    }
}