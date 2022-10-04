package com.hallett.taskassistant.features.taskList.corndux

import com.hallett.corndux.Action
import com.hallett.corndux.Reducer
import com.hallett.taskassistant.main.corndux.UpdateExpandedTask
import com.hallett.taskassistant.main.corndux.UpdateSelectedScope
import com.hallett.taskassistant.main.corndux.UpdateTaskList
import com.hallett.taskassistant.ui.genericTaskList.CancelRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.ClickRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.SubmitRescheduleTask

class TaskListReducer : Reducer<TaskListState> {
    override fun reduce(state: TaskListState, action: Action): TaskListState {
        return when (action) {
            is ClickRescheduleTask -> state.copy(
                currentlySchedulingTask = action.task
            )
            is CancelRescheduleTask -> state.copy(
                currentlySchedulingTask = null
            )
            is SubmitRescheduleTask -> state.copy(
                currentlySchedulingTask = null
            )
            is UpdateSelectedScope -> state.copy(scope = action.scope)
            is UpdateTaskList -> state.copy(taskList = action.taskList)
            is UpdateExpandedTask -> {
                val newTask = when (state.currentlyExpandedTask) {
                    action.task -> null
                    else -> action.task
                }
                state.copy(currentlyExpandedTask = newTask)
            }
            else -> state
        }
    }
}