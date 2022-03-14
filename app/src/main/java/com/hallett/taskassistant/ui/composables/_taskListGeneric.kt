package com.hallett.taskassistant.ui.composables

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
import com.hallett.taskassistant.corndux.performers.actions.DeferTask
import com.hallett.taskassistant.corndux.performers.actions.DeleteTask
import com.hallett.taskassistant.corndux.performers.actions.MarkTaskAsComplete
import com.hallett.taskassistant.corndux.performers.actions.MarkTaskAsIncomplete
import com.hallett.taskassistant.corndux.performers.actions.RescheduleTask
import com.hallett.taskassistant.ui.model.TaskAction
import com.hallett.taskassistant.ui.model.TaskView
import taskAssistantStore

@Composable
fun TaskList(
    pagedTasks: LazyPagingItems<TaskView>,
    isTaskExpanded: (Task) -> Boolean,
    onTaskClickedAction: (Task) -> Action,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        items(pagedTasks) { taskView ->
            when (taskView) {
                null -> {}
                is TaskView.HeaderHolder -> Text(taskView.text, style = MaterialTheme.typography.h6)
                is TaskView.TaskHolder -> TaskItem(
                    taskHolder = taskView,
                    isExpanded = isTaskExpanded(taskView.task),
                    onClickedAction = onTaskClickedAction(taskView.task),
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    taskHolder: TaskView.TaskHolder,
    isExpanded: Boolean,
    onClickedAction: Action,
    modifier: Modifier = Modifier,
) {
    val store by taskAssistantStore()
    Card(modifier = modifier
        .clickable { store.dispatch(onClickedAction) }
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
fun TaskActions(task: Task, actions: List<TaskAction>) {
    val store by taskAssistantStore()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        actions.forEach { action ->
            when (action) {
                TaskAction.DEFER -> DeferTaskButton {
                    store.dispatch(DeferTask(task))
                }
                TaskAction.DELETE -> DeleteTaskButton {
                    store.dispatch(DeleteTask(task))
                }
                TaskAction.COMPLETE -> CompleteTaskButton {
                    store.dispatch(MarkTaskAsComplete(task))
                }
                TaskAction.UNCOMPLETE -> UncompleteTaskButton {
                    store.dispatch(MarkTaskAsIncomplete(task))
                }
                TaskAction.RESCHEDULE -> RescheduleTaskButton {
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