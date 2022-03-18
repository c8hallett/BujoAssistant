package com.hallett.taskassistant.createTasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.CancelTask
import com.hallett.taskassistant.corndux.SubmitTask
import com.hallett.taskassistant.corndux.overrideStoreType
import com.hallett.taskassistant.createTasks.corndux.CreateTaskState
import com.hallett.taskassistant.ui.composables.ScopeSelection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI


@FlowPreview
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@Composable
fun TaskCreation() {
    subDI(
        diBuilder = { overrideStoreType<Store<CreateTaskState>>() }
    ) {
        val store by rememberInstance<Store<CreateTaskState>>()
        val createTaskInfo by store.observeState().collectAsState()
        val shouldExpandCard = createTaskInfo.scopeSelectionInfo == null

        var taskName by remember { mutableStateOf("") }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
            val cardModifier = if (shouldExpandCard) Modifier else Modifier.weight(1.0f)

            Card(backgroundColor = MaterialTheme.colors.surface, modifier = cardModifier) {
                Column(modifier = Modifier.padding(12.dp)) {
                    BasicTextField(
                        value = taskName,
                        onValueChange = { newTaskName: String ->
                            taskName = newTaskName
                        },
                        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onSurface),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ScopeSelection(
                        scope = createTaskInfo.scope,
                        scopeSelectionInfo = createTaskInfo.scopeSelectionInfo,
                    )
                }
            }

            TaskSelectionButtons(
                onTaskSubmitted = { store.dispatch(SubmitTask(taskName.trimExtraSpaces())) },
                onTaskCancelled = { store.dispatch(CancelTask) }
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

private fun String.trimExtraSpaces(): String {
    var foundChar = false
    return this
        .trimStart()
        .foldRightIndexed("") { index, character, acc ->
            when {
                foundChar -> character + acc
                character.isWhitespace() -> when {
                    // current character is a double space
                    this.getOrNull(index - 1)?.isWhitespace() == true -> acc
                    else -> {
                        foundChar = true
                        character + acc
                    }
                }
                else -> {
                    foundChar = true
                    character + acc
                }
            }
        }
}