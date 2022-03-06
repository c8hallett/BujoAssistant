package com.hallett.taskassistant.ui.composables

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.actions.CancelTask
import com.hallett.taskassistant.corndux.actions.SubmitTask
import com.hallett.taskassistant.corndux.TaskAssistantState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.compose.rememberInstance
import taskAssistantStore

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


@FlowPreview
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@Composable
fun TaskCreation() {
    val store by taskAssistantStore()
    val createTaskInfo by store.observeState { it.components.createTask }.collectAsState()
    val shouldExpandCard = createTaskInfo.scopeSelectionInfo == null

    var taskName by remember{ mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
        val cardModifier = if(shouldExpandCard) Modifier else Modifier.weight(1.0f)

        Card(backgroundColor = MaterialTheme.colors.surface, modifier = cardModifier) {
            Column(modifier = Modifier.padding(12.dp)) {
                BasicTextField(
                    value = taskName,
                    onValueChange = { newTaskName ->
                        taskName = newTaskName
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = TaskNameVisualizer(),
                )
                ScopeSelection(createTaskInfo.scope, createTaskInfo.scopeSelectionInfo)
            }
        }

        TaskSelectionButtons(
            onTaskSubmitted = { store.dispatch(SubmitTask(taskName.trimExtraSpaces())) },
            onTaskCancelled = { store.dispatch(CancelTask) }
        )
    }
}

fun CoroutineScope.debounce(
    waitMs: Long = 300L,
    destinationFunction: () -> Unit
): () -> Unit {
    var debounceJob: Job? = null
    return {
        debounceJob?.cancel()
        debounceJob = launch {
            delay(waitMs)
            destinationFunction()
        }
    }
}

@Composable
@Preview
fun TaskSelectionButtonsPreview() {
    TaskSelectionButtons(onTaskSubmitted = {}, onTaskCancelled = {})
}

private class TaskNameVisualizer : VisualTransformation {
    private companion object {
        const val PREFIX_STRING = "I want to "
        const val DEFAULT_STRING = " "
        val FONT_SIZE = 24.sp
    }

    private val transformedOffsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = offset + PREFIX_STRING.length
        override fun transformedToOriginal(offset: Int): Int = offset - PREFIX_STRING.length
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val prefix = AnnotatedString(
            PREFIX_STRING,
            SpanStyle(fontSize = FONT_SIZE)
        )

        val nonNullText = when {
            text.text.isBlank() -> DEFAULT_STRING
            else -> text.text
        }

        val withBackground = AnnotatedString(
            nonNullText,
            SpanStyle(
                background = Color.LightGray, // TODO: i really want to know how i can apply a themed color here
                fontWeight = FontWeight.Bold,
                fontSize = FONT_SIZE
            )
        )
        return TransformedText(prefix + withBackground, transformedOffsetMapping)
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