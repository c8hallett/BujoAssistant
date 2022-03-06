package com.hallett.taskassistant.corndux.actions

import com.hallett.corndux.Action
import com.hallett.domain.model.Task

object PerformInitialSetup: Action

class SubmitTask(val taskName: String): Action
object CancelTask: Action

object LoadLargerScope: Action
object LoadSmallerScope: Action

object AddRandomOverdueTask: Action

data class TaskClickedInList(val task: Task): Action
data class DeleteTask(val task: Task): Action
data class DeferTask(val task: Task): Action
data class RescheduleTask(val task: Task): Action
data class CompleteTask(val task: Task): Action