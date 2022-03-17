package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Action
import com.hallett.corndux.Event
import com.hallett.taskassistant.corndux.IInterpreter
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.corndux.interpreters.NewScopeClicked
import com.hallett.taskassistant.corndux.interpreters.NewScopeTypeClicked
import com.hallett.taskassistant.corndux.interpreters.ScopeSelectionCancelled
import com.hallett.taskassistant.corndux.interpreters.ScopeSelectionEntered
import com.hallett.taskassistant.corndux.interpreters.TaskInListClicked
import com.hallett.taskassistant.corndux.performers.actions.TaskListAction
import org.kodein.di.bindSingleton
import org.kodein.di.compose.subDI
import org.kodein.di.instance
import taskAssistantStore


class TaskListInterpreter(store: IStore): IInterpreter(store) {
    override fun mapEvent(event: Event): Action? = when(event) {
        is NewScopeClicked -> TaskListAction.SelectNewScope(event.newTaskScope)
        is NewScopeTypeClicked -> TaskListAction.SelectNewScopeType(event.scopeType)
        ScopeSelectionCancelled -> TaskListAction.CancelScopeSelection
        ScopeSelectionEntered -> TaskListAction.EnterScopeSelection
        is TaskInListClicked -> TaskListAction.ClickTaskInList(event.task)
        else -> null
    }
}
@Composable
fun OpenTaskList() {
    subDI(diBuilder = {
        bindSingleton { TaskListInterpreter(instance()) }
    }) {
        val store by taskAssistantStore()
        val state by store.observeState { it.components.taskList }.collectAsState()
        val pagedTasks = state.taskList.collectAsLazyPagingItems()
        val isSelectActive = state.scopeSelectionInfo != null


        Column {
            val scopeSelectionHeight = when {
                isSelectActive -> Modifier.fillMaxHeight(0.38f)
                else -> Modifier.wrapContentHeight()
            }

            ScopeSelection(
                scope = state.scope,
                scopeSelectionInfo = state.scopeSelectionInfo,
                modifier = scopeSelectionHeight.padding(horizontal = 12.dp)
            )

            if (pagedTasks.itemCount == 0) {
                Text("No tasks!")
            } else {
                TaskList(
                    pagedTasks = pagedTasks,
                    isTaskExpanded = { it == state.currentlyExpandedTask },
                )
            }
        }
    }
}
