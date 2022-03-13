package com.hallett.taskassistant.corndux.performers.actions

import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.taskassistant.corndux.FutureTaskListState

class SubmitTask(val taskName: String) : Action
object CancelTask : Action

object LoadLargerScope : Action
object LoadSmallerScope : Action

object AddRandomOverdueTask : Action

data class ExpandList(val list: FutureTaskListState.ExpandedList) : Action
data class TaskClickedInList(val task: Task) : Action

sealed interface TaskAction : Action
data class DeleteTask(val task: Task) : TaskAction
data class DeferTask(val task: Task) : TaskAction
data class RescheduleTask(val task: Task) : TaskAction
data class MarkTaskAsComplete(val task: Task) : TaskAction
data class MarkTaskAsIncomplete(val task: Task) : TaskAction