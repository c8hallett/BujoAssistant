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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.hallett.corndux.Action
import com.hallett.taskassistant.features.limboTasks.corndux.LimboStore
import com.hallett.taskassistant.features.genericTaskList.TaskList
import com.hallett.taskassistant.util.debounce
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.compose.rememberInstance

@Composable
fun FutureTaskList() {
    val limboStore by rememberInstance<LimboStore>()
    WithStore(limboStore) {
        val state by limboStore.observeState().collectAsState()
        val taskList = state.list.collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            SearchField()
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
fun SearchField() {
    val store = LocalStore.current
    val scope: CoroutineScope by rememberInstance()
    val dispatchNewSearch: (String) -> Unit = remember {
        // need the same debounce function to persist over recomposition
        scope.debounce(250L) { store.dispatch(SearchUpdated(it)) }
    }

    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = {
            text = it
            dispatchNewSearch(it)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth()
    )
}