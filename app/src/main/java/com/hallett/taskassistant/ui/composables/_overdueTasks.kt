package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Action
import com.hallett.corndux.Event
import com.hallett.taskassistant.corndux.IInterpreter
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.corndux.interpreters.TaskInListClicked
import com.hallett.taskassistant.corndux.performers.actions.AddRandomOverdueTask
import com.hallett.taskassistant.corndux.performers.actions.FutureTaskAction
import com.hallett.taskassistant.corndux.performers.actions.OverdueTaskAction
import org.kodein.di.bindSingleton
import org.kodein.di.compose.subDI
import org.kodein.di.instance
import taskAssistantStore

class OverdueTaskInterpreter(store: IStore): IInterpreter(store) {
    override fun mapEvent(event: Event): Action? = when (event){
        is TaskInListClicked -> OverdueTaskAction.ClickTaskInList(event.task)
        else -> null
    }
}

@Composable
fun OverdueTasks() {
    subDI(diBuilder = {
        bindSingleton { OverdueTaskInterpreter(instance()) }
    }) {
        val store by taskAssistantStore()
        val state by store.observeState { it.components.overdueTask }.collectAsState()
        val pagedTasks = state.taskList.collectAsLazyPagingItems()

        Column {
            Text("Overdue tasks", style = MaterialTheme.typography.h6)
            Button(onClick = { store.dispatch(AddRandomOverdueTask) }) {
                Text("Random Overdue Task")
            }

            if (pagedTasks.itemCount == 0) {
                Text("No overdue tasks!")
            } else {
                TaskList(
                    pagedTasks = pagedTasks,
                    isTaskExpanded = { state.currentlyExpandedTask == it },
                )
            }
        }
    }
}
