package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.domain.usecase.scope_operations.INormalizeDateForScopeUseCase
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*

class ObserveCurrentTasksFlowableUseCase(
    private val taskDao: BujoTaskDao,
    private val scopeMapper: Mapper<DScopeInstance?, PScopeInstance>,
    private val normalizeDateForScopeUseCase: INormalizeDateForScopeUseCase
): IObserveCurrentTasksFlowableUseCase {
    override fun execute(): Flow<List<Task>> {
        val currentDate = Date()
        val dayTruncatedDate = normalizeDateForScopeUseCase.execute(DScope.DAY, currentDate)
        val weekTruncatedDate = normalizeDateForScopeUseCase.execute(DScope.WEEK, currentDate)

        val dayScope = scopeMapper.map(DScopeInstance(DScope.DAY, dayTruncatedDate))
        val dayFlow = taskDao.getAllTaskForScopeInstance(DScope.DAY, dayTruncatedDate).map { taskList ->
            taskList.map {
                Task(
                    id = it.id,
                    taskName = it.taskName,
                    scopeInstance = dayScope,
                    status = it.status,
                    isCurrentScope = true
                )
            }
        }

        val weekScope = scopeMapper.map(DScopeInstance(DScope.WEEK, dayTruncatedDate))
        val weekFlow = taskDao.getAllTaskForScopeInstance(DScope.WEEK, weekTruncatedDate).map { taskList ->
            taskList.map {
                Task(
                    id = it.id,
                    taskName = it.taskName,
                    scopeInstance = weekScope,
                    status = it.status,
                    isCurrentScope = true
                )
            }
        }

        return dayFlow.combine(weekFlow) { day, week -> day + week }
    }

}