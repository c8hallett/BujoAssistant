package com.hallett.taskassistant.features.limboTasks

import LocalStore
import WithStore
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import collectState
import com.hallett.corndux.Action
import com.hallett.taskassistant.ui.genericTaskList.TaskList
import com.hallett.taskassistant.features.limboTasks.corndux.LimboStore
import org.kodein.di.compose.rememberInstance

@Composable
fun FutureTaskList() {
    val limboStore by rememberInstance<LimboStore>()
    WithStore(limboStore) {
        val state by limboStore.collectState()
        val taskList = state.list.collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            SearchField(state.search)
            TaskList(
                pagedTasks = taskList,
                isTaskExpanded = { state.expandedTask == it },
                modifier = Modifier.weight(1.0f),
            )
        }
    }
}

data class SearchUpdated(val newSearch: String) : Action

@Composable
fun SearchField(searchText: String) {
    val store = LocalStore.current

    TextField(
        value = searchText,
        onValueChange = { store.dispatch(SearchUpdated(it)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth()
    )
}