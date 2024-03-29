package com.hallett.taskassistant.features.createTasks

import WithStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import collectState
import com.hallett.taskassistant.features.createTasks.corndux.CreateTaskStore
import com.hallett.taskassistant.features.scopeSelection.ScopeSelection
import com.hallett.taskassistant.main.corndux.CancelTask
import com.hallett.taskassistant.main.corndux.OpenTask
import com.hallett.taskassistant.main.corndux.SubmitTask
import com.hallett.taskassistant.main.corndux.UpdateTaskName
import org.kodein.di.compose.rememberInstance


@Composable
fun TaskCreation(taskId: Long) {
    val createTaskStore by rememberInstance<CreateTaskStore>()

    LaunchedEffect(key1 = taskId) {
        createTaskStore.dispatch(OpenTask(taskId))
    }

    WithStore(createTaskStore) {
        val createTaskInfo by createTaskStore.collectState()
        val shouldExpandCard = createTaskInfo.scopeSelectionInfo == null

        Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
            val cardModifier = if (shouldExpandCard) Modifier else Modifier.weight(1.0f)

            Card(backgroundColor = MaterialTheme.colors.surface, modifier = cardModifier) {
                Column(modifier = Modifier.padding(12.dp)) {
                    BasicTextField(
                        value = createTaskInfo.task.name,
                        onValueChange = { createTaskStore.dispatch(UpdateTaskName(it)) },
                        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onSurface),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ScopeSelection(
                        scope = createTaskInfo.task.scope,
                        scopeSelectionInfo = createTaskInfo.scopeSelectionInfo,
                    )
                }
            }

            TaskSelectionButtons(
                onTaskSubmitted = { createTaskStore.dispatch(SubmitTask) },
                onTaskCancelled = { createTaskStore.dispatch(CancelTask) }
            )
        }
    }
}

@Composable
fun TaskSelectionButtons(onTaskSubmitted: () -> Unit, onTaskCancelled: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Button(
            onClick = onTaskCancelled,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
        ) {
            Text("Nevermind", color = MaterialTheme.colors.onError)
        }
        Button(
            onClick = onTaskSubmitted,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            Text("Yeah, that's right", color = MaterialTheme.colors.onPrimary)
        }
    }
}