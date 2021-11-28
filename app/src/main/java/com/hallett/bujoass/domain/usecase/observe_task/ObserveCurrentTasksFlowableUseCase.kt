package com.hallett.bujoass.domain.usecase.observe_task

import com.hallett.bujoass.database.task.BujoTaskDao
import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.ScopeType
import com.hallett.bujoass.domain.usecase.mapper.Mapper
import com.hallett.bujoass.presentation.model.Task
import com.hallett.scopes.IScopeGenerator
import com.hallett.scopes.ScopeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.*

class ObserveCurrentTasksFlowableUseCase(
    private val taskDao: BujoTaskDao,
    private val taskMapper: Mapper<BujoTaskEntity, Task>,
    private val scopeGenerator: IScopeGenerator,
): IObserveCurrentTasksFlowableUseCase {
    override fun execute(): Flow<List<Task>> {
        Timber.i("Getting current tasks")
        val currentDayScope = scopeGenerator.generateScope(ScopeType.DAY)
        val dayFlow = taskDao.getAllTaskForScopeInstance(currentDayScope).map { taskList ->

            taskList.map { taskMapper.map(it) }
        }

        val currentWeekScope = scopeGenerator.generateScope(ScopeType.WEEK)
        val weekFlow = taskDao.getAllTaskForScopeInstance(currentWeekScope).map { taskList ->
            taskList.map { taskMapper.map(it) }
        }

        return dayFlow.combine(weekFlow) { day, week -> day + week }
    }

}