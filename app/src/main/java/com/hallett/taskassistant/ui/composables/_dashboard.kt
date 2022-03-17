package com.hallett.taskassistant.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Action
import com.hallett.corndux.Event
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.corndux.IInterpreter
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.corndux.interpreters.TaskInListClicked
import com.hallett.taskassistant.corndux.performers.actions.DashboardAction
import com.hallett.taskassistant.corndux.performers.actions.LoadLargerScope
import com.hallett.taskassistant.corndux.performers.actions.LoadSmallerScope
import org.kodein.di.bindSingleton
import org.kodein.di.compose.subDI
import org.kodein.di.instance
import taskAssistantStore

class DashboardInterpreter(store: IStore): IInterpreter(store) {
    override fun mapEvent(event: Event): Action? = when (event){
        is TaskInListClicked -> DashboardAction.ClickTaskInList(event.task)
        else -> null
    }
}

@Composable
fun TaskDashboard() {
    subDI(diBuilder = {
        bindSingleton { DashboardInterpreter(instance()) }
    }) {
        val store by taskAssistantStore()
        val state by store.observeState { it.components.dashboard }.collectAsState()
        val taskList = state.taskList.collectAsLazyPagingItems()

        Column {
            TaskDashboardHeader(scopeType = state.scopeType) {
                store.dispatch(LoadSmallerScope)
            }
            if (taskList.itemCount == 0) {
                Text(
                    "No current tasks!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                TaskList(
                    pagedTasks = taskList,
                    isTaskExpanded = { state.currentlyExpandedTask == it },
                    modifier = Modifier.weight(1.0f)
                )
            }
            TaskDashboardFooter(scopeType = state.scopeType) {
                store.dispatch(LoadLargerScope)
            }
        }
    }
}

@Composable
fun TaskDashboardHeader(scopeType: ScopeType, onHeaderClicked: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize()
        .clickable { onHeaderClicked() }) {
        // for all scope types that come before selected one
        ScopeType.values().take(scopeType.ordinal).forEach {
            TaskDashboardLabel(scopeType = it, highlighted = false)
        }
        TaskDashboardLabel(scopeType = scopeType, highlighted = true)
    }
}

@Composable
fun TaskDashboardFooter(scopeType: ScopeType, onFooterClicked: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize()
        .clickable { onFooterClicked() }) {
        ScopeType.values().run {
            // for all scope types that come after selected one
            takeLast(lastIndex - scopeType.ordinal).forEach {
                TaskDashboardLabel(scopeType = it, highlighted = false)
            }
        }
    }
}

@Composable
fun TaskDashboardLabel(scopeType: ScopeType, highlighted: Boolean) {
    val label = when (scopeType) {
        ScopeType.DAY -> "today"
        ScopeType.WEEK -> "this week"
        ScopeType.MONTH -> "this month"
        ScopeType.YEAR -> "this year"
    }

    val typography = if (highlighted) MaterialTheme.typography.h3 else MaterialTheme.typography.h6
    val textColor = if (highlighted) MaterialTheme.colors.onSurface else Color.LightGray
    Text(
        label,
        style = typography,
        color = textColor,
    )
}

@Composable
@Preview
fun HeaderFooterPreview() {
    val selectedScopeType = ScopeType.WEEK
    Column {
        TaskDashboardHeader(scopeType = selectedScopeType) {}
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), color = Color.White
        ) {}
        TaskDashboardFooter(scopeType = selectedScopeType) {}
    }
}