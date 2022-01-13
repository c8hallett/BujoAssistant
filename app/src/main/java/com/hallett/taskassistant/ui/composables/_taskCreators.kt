package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.ui.model.task.WritableTask

@Composable
fun TaskEditScreen(
    scope: Scope?,
    onTaskSubmitted: (WritableTask?) -> Unit,
    onScopeClicked: () -> Unit,
) {
    val (text, setText) = remember{ mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
        Text(
            "I want to",
            style = MaterialTheme.typography.h4
        )
        TextField(
            value = text,
            onValueChange = setText,
            label = { Text("Task name") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth()){
            Button(onClick = { onScopeClicked() }) {
                Text(scope?.toString() ?: "Sometime",
                    style = MaterialTheme.typography.subtitle2
                )
            }
            IconButton(onClick = {
                onTaskSubmitted(WritableTask(text, scope))
            }) {
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
        scope = null,
        onTaskSubmitted = { },
        onScopeClicked = { }
    )
}