package com.hallett.taskassistant.corndux.performers.actions

import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.taskassistant.corndux.FutureTaskListState

class SubmitTask(val taskName: String): Action
object CancelTask: Action

object LoadLargerScope: Action
object LoadSmallerScope: Action

object AddRandomOverdueTask: Action

data class ExpandList(val list: FutureTaskListState.ExpandedList): Action

data class TaskClickedInList(val task: Task): Action
data class DeleteTask(val task: Task): Action
data class DeferTask(val task: Task): Action
data class RescheduleTask(val task: Task): Action
data class ToggleTaskComplete(val task: Task): Action