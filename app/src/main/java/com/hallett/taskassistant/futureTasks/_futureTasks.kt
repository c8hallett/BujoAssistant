package com.hallett.taskassistant.futureTasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.collectAsLazyPagingItems
import closestStore
import com.hallett.corndux.Action
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.overrideStoreType
import com.hallett.taskassistant.futureTasks.corndux.ExpandList
import com.hallett.taskassistant.futureTasks.corndux.FutureState
import com.hallett.taskassistant.futureTasks.corndux.ListType
import com.hallett.taskassistant.ui.composables.TaskList
import com.hallett.taskassistant.util.debounce
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI

@Composable
fun FutureTaskList() {
    subDI(
        diBuilder = { overrideStoreType<Store<FutureState>>() }
    ) {
        val store by rememberInstance<Store<FutureState>>()
        val state by store.observeState().collectAsState()
        val taskList = state.list.collectAsLazyPagingItems()

        val tabs: List<Pair<ListType, String>> = listOf(
            Pair(ListType.UNSCHEDULED, "Sometime"),
            Pair(ListType.SCHEDULED, "Scheduled")
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )  {
            SearchField()
            TabRow(selectedTabIndex = tabs.indexOfFirst { it.first == state.listType }) {
                tabs.forEach {
                    val (listType, label) = it
                    Tab(
                        selected = state.listType == listType,
                        onClick = { store.dispatch(ExpandList(listType)) },
                        text = { Text(text = label) }
                    )
                }
            }
            TaskList(
                pagedTasks = taskList,
                isTaskExpanded = { state.expandedTask == it },
                modifier = Modifier.weight(1.0f),
            )
        }
    }
}

data class SearchUpdated(val newSearch: String): Action
@Composable
fun ColumnScope.SearchField() {
    val store by closestStore()
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