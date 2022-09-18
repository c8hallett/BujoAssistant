package com.hallett.taskassistant.features.genericTaskList

import WithStore
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.hallett.corndux.Action
import com.hallett.domain.model.Task
import com.hallett.domain.model.TaskStatus
import com.hallett.taskassistant.LocalStore
import com.hallett.taskassistant.features.genericTaskList.corndux.TaskActionsStore
import org.kodein.di.compose.rememberInstance


sealed interface TaskAction : Action
data class ClickTaskInList(val task: Task) : TaskAction
data class DeleteTask(val task: Task) : TaskAction
data class DeferTask(val task: Task) : TaskAction
data class RescheduleTask(val task: Task) : TaskAction
data class MarkTaskAsComplete(val task: Task) : TaskAction
data class MarkTaskAsIncomplete(val task: Task) : TaskAction

@Composable
fun TaskList(
    pagedTasks: LazyPagingItems<TaskView>,
    isTaskExpanded: (Task) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val taskActionStore by rememberInstance<TaskActionsStore>()
    WithStore(taskActionStore) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = modifier
        ) {
            items(pagedTasks) { taskView ->
                when (taskView) {
                    null -> {}
                    is TaskView.HeaderHolder -> Text(
                        taskView.text,
                        style = MaterialTheme.typography.h6
                    )
                    is TaskView.TaskHolder -> TaskItem(
                        taskHolder = taskView,
                        isExpanded = isTaskExpanded(taskView.task),
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    taskHolder: TaskView.TaskHolder,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val store = LocalStore.current
    Card(modifier = modifier
        .clickable { store.dispatch(ClickTaskInList(taskHolder.task)) }
        .fillMaxWidth()
        .animateContentSize()
    ) {
        taskHolder.run {
            Column {
                val textDecoration =
                    if (task.status == TaskStatus.COMPLETE) TextDecoration.LineThrough else TextDecoration.None
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.h6.copy(textDecoration = textDecoration),
                    modifier = Modifier.padding(12.dp)
                )
                if (isExpanded) {
                    TaskActions(
                        task = task,
                        actions = actions
                    )
                }
            }
        }
    }
}

@Composable
fun TaskActions(task: Task, actions: List<TaskActionType>) {
    val store = LocalStore.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        actions.forEach { action ->
            when (action) {
                TaskActionType.DEFER -> DeferTaskButton {
                    store.dispatch(DeferTask(task))
                }
                TaskActionType.DELETE -> DeleteTaskButton {
                    store.dispatch(DeleteTask(task))
                }
                TaskActionType.COMPLETE -> CompleteTaskButton {
                    store.dispatch(MarkTaskAsComplete(task))
                }
                TaskActionType.UNCOMPLETE -> UncompleteTaskButton {
                    store.dispatch(MarkTaskAsIncomplete(task))
                }
                TaskActionType.RESCHEDULE -> RescheduleTaskButton {
                    store.dispatch(RescheduleTask(task))
                }
            }
        }
    }
}

@Composable
fun DeferTaskButton(defer: () -> Unit) {
    IconButton(onClick = defer) {
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "defer task")
    }
}

@Composable
fun DeleteTaskButton(delete: () -> Unit) {
    IconButton(onClick = delete) {
        Icon(Icons.Default.Delete, contentDescription = "delete task")
    }
}

@Composable
fun CompleteTaskButton(complete: () -> Unit) {
    IconButton(onClick = complete) {
        Icon(Icons.Default.CheckCircle, contentDescription = "complete task")
    }
}

@Composable
fun UncompleteTaskButton(uncomplete: () -> Unit) {
    IconButton(onClick = uncomplete) {
        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "un-complete task")
    }
}

@Composable
fun RescheduleTaskButton(reschedule: () -> Unit) {
    IconButton(onClick = reschedule) {
        Icon(Icons.Default.Schedule, contentDescription = "reschedule task")
    }
}