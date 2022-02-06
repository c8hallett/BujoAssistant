package com.hallett.taskassistant.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavController
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.ui.viewmodels.ScopeSelectionViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskEditViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.compose.withDI

@Composable
fun TaskSelectionButtons(onTaskSubmitted: () -> Unit, onTaskCancelled: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ){
        Button(onClick = onTaskCancelled, colors =  ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)) {
            Text("Nevermind", color = MaterialTheme.colors.onError)
        }
        Button(onClick = onTaskSubmitted, colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)){
            Text("Yeah, that's right", color = MaterialTheme.colors.onPrimary)
        }
    }
}


@FlowPreview
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun TaskEdit(taskEditVm: TaskEditViewModel, scopeSelectionVm: ScopeSelectionViewModel, di: DI, navController: NavController, taskId: Long) = withDI(di) {

    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val taskName by taskEditVm.getTaskName(taskId = taskId).collectAsState(initial = "")
    val selectedScope by taskEditVm.observeSelectedScope().collectAsState(initial = null)
    val selectedScopeType by scopeSelectionVm.observeScopeType().collectAsState(initial = ScopeType.WEEK)
    val (isSelectActive, setSelectActive) = remember{ mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
        val cardModifier = when(isSelectActive) {
            true -> Modifier.weight(1.0f)
            else -> Modifier
        }
        Card(backgroundColor = MaterialTheme.colors.surface, modifier = cardModifier){
            Column(modifier = Modifier.padding(12.dp)) {
                BasicTextField(
                    value = taskName,
                    onValueChange = { newTaskName ->
                        taskEditVm.setTaskName(newTaskName.trimStart().trimEndExtra())
                    } ,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = TaskNameVisualizer(),
                )
                ScopeSelection(
                    scope = selectedScope,
                    scopeType = selectedScopeType,
                    isSelectActive = isSelectActive,
                    scopes = scopeSelectionVm.observeScopeSelectorList(),
                    onScopeTypeSelected = { newScopeType ->
                        scopeSelectionVm.onNewScopeTypeSelected(newScopeType)
                    },
                    onScopeSelected = { newScope ->
                        taskEditVm.setTaskScope(newScope)
                        coroutineScope.launch {
                            modalState.hide()
                        }
                    },
                    setSelectActive = setSelectActive,
                )
            }
        }
        TaskSelectionButtons(
            onTaskSubmitted = {
                taskEditVm.onTaskSubmitted()
                navController.popBackStack()
            },
            onTaskCancelled = {
                navController.popBackStack()
            }
        )
    }
}

@Composable
@Preview
fun TaskSelectionButtonsPreview() {
    TaskSelectionButtons(onTaskSubmitted = {}, onTaskCancelled = {})
}

private class TaskNameVisualizer(): VisualTransformation {
    private companion object {
        const val PREFIX_STRING = "I want to "
        const val DEFAULT_STRING = " ... "
        val FONT_SIZE = 24.sp
    }

    private val transformedOffsetMapping = object: OffsetMapping {
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

private fun String.trimEndExtra(): String {
    var foundChar = false
    return this.foldRightIndexed(""){ index, character, acc ->
        when{
            foundChar -> character + acc
            character.isWhitespace() -> when{
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