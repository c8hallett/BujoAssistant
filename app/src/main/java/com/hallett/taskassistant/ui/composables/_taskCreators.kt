package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hallett.scopes.model.Scope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun TaskEditScreen(
    taskNameFlow: Flow<String>,
    scope: Scope?,
    onTaskNameUpdated: (String) -> Unit,
    onTaskSubmitted: () -> Unit,
    onScopeClicked: () -> Unit,
) {
    val taskName by taskNameFlow.collectAsState(initial = "")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
        Text(
            "I want to",
            style = MaterialTheme.typography.h4
        )
        TextField(
            value = taskName,
            onValueChange = onTaskNameUpdated,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth()){
            Button(onClick = { onScopeClicked() }) {
                Text(scope?.toString() ?: "Sometime",
                    style = MaterialTheme.typography.subtitle2
                )
            }
            IconButton(onClick = onTaskSubmitted) {
                Icon(Icons.Default.Send,
                    contentDescription = "save task"
                )
            }
        }
    }
}

@Composable
@Preview
fun TaskCreationPreview() {
    TaskEditScreen(
        taskNameFlow = flowOf("Do something"),
        scope = null,
        onTaskNameUpdated = {},
        onTaskSubmitted = { },
        onScopeClicked = { },
    )
}