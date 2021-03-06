package com.hallett.taskassistant.corndux.reducers

import com.hallett.corndux.Commit
import com.hallett.taskassistant.corndux.CreateTaskState
import com.hallett.taskassistant.corndux.DashboardState
import com.hallett.taskassistant.corndux.FutureTaskListState
import com.hallett.taskassistant.corndux.IReducer
import com.hallett.taskassistant.corndux.OverdueTasksState
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.corndux.TasksListState

class ComponentReducer : IReducer {
    override fun reduce(state: TaskAssistantState, commit: Commit): TaskAssistantState =
        when (commit) {
            is UpdateCreateTaskSelectedScope -> state.updateCreateTask { copy(scope = commit.scope) }
            is UpdateCreateTaskScopeSelectionInfo -> state.updateCreateTask {
                copy(
                    scopeSelectionInfo = commit.scopeSelectionInfo
                )
            }
            is ClearCreateTaskState -> state.updateCreateTask { CreateTaskState() }

            is UpdateDashboardTaskList -> state.updateDashboard {
                copy(
                    scopeType = commit.scopeType,
                    taskList = commit.taskList
                )
            }
            is UpdateDashboardExpandedTask -> state.updateDashboard {
                copy(currentlyExpandedTask = commit.task)
            }

            is UpdateOverdueTaskList -> state.updateOverdueTask { copy(taskList = commit.taskList) }
            is UpdateOverdueExpandedTask -> state.updateOverdueTask {
                copy(currentlyExpandedTask = commit.task)
            }
            is UpdateTaskListTaskList -> state.updateTaskList { copy(taskList = commit.taskList) }
            is UpdateTaskListSelectedScope -> state.updateTaskList { copy(scope = commit.scope) }
            is UpdateTaskListScopeSelectionInfo -> state.updateTaskList { copy(scopeSelectionInfo = commit.scopeSelectionInfo) }
            is UpdateTaskListExpandedTask -> state.updateTaskList {
                copy(currentlyExpandedTask = commit.task)
            }

            is UpdateCurrentlyExpandedList -> state.updateFutureTaskList { copy(expandedList = commit.list) }
            is UpdateFutureTaskLists -> state.updateFutureTaskList {
                copy(
                    unscheduledList = commit.unscheduled,
                    scheduledList = commit.scheduled
                )
            }
            is UpdateFutureExpandedTask -> state.updateFutureTaskList {
                copy(currentlyExpandedTask = commit.task)
            }
            else -> state
        }

    private inline fun TaskAssistantState.updateCreateTask(update: CreateTaskState.() -> CreateTaskState): TaskAssistantState {
        return updateComponents { copy(createTask = createTask.update()) }
    }

    private inline fun TaskAssistantState.updateDashboard(update: DashboardState.() -> DashboardState): TaskAssistantState {
        return updateComponents { copy(dashboard = dashboard.update()) }
    }

    private inline fun TaskAssistantState.updateTaskList(update: TasksListState.() -> TasksListState): TaskAssistantState {
        return updateComponents { copy(taskList = taskList.update()) }
    }

    private inline fun TaskAssistantState.updateOverdueTask(update: OverdueTasksState.() -> OverdueTasksState): TaskAssistantState {
        return updateComponents { copy(overdueTask = overdueTask.update()) }
    }

    private inline fun TaskAssistantState.updateFutureTaskList(update: FutureTaskListState.() -> FutureTaskListState): TaskAssistantState {
        return updateComponents { copy(futureTasks = futureTasks.update()) }
    }
}