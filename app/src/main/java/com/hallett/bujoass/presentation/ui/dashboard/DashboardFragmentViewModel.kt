package com.hallett.bujoass.presentation.ui.dashboard

import com.hallett.bujoass.domain.usecase.observe_task.IObserveCurrentTasksFlowableUseCase
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.ui.BujoAssViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

class DashboardFragmentViewModel(
    private val observeCurrentTasksFlowableUseCase: IObserveCurrentTasksFlowableUseCase
): BujoAssViewModel() {

    fun observeDashboardItems(): Flow<List<DashboardItem>> {
        return observeCurrentTasksFlowableUseCase.execute()
            .catch { t ->
                Timber.w(t, "Some error occurred")
            }.map { list ->
                val (day, week) = list
                    .map { DashboardItem.TaskItem(it) }
                    .partition { it.task.scopeInstance.scope == PScope.DAY }

                val todayHeader = listOf(DashboardItem.HeaderItem("TODAY"))
                val thisWeekHeader = listOf(DashboardItem.HeaderItem("THIS WEEK"))

                todayHeader + day + thisWeekHeader + week
            }
    }
}