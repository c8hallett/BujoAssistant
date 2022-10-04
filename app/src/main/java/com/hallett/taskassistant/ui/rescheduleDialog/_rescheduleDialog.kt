package com.hallett.taskassistant.ui.rescheduleDialog

import LocalStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hallett.domain.model.Task
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.features.scopeSelection.ScopeSelection
import com.hallett.taskassistant.ui.genericTaskList.CancelRescheduleTask
import com.hallett.taskassistant.ui.genericTaskList.SubmitRescheduleTask

@Composable
fun RescheduleTaskDialog(task: Task) {
    val store = LocalStore.current
    var newScope: Scope? by remember(task) {
        mutableStateOf(task.scope)
    }
    Dialog(
        onDismissRequest = { store.dispatch(CancelRescheduleTask) },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .background(MaterialTheme.colors.background)
        ) {

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = task.name)
                ScopeSelection(scope = newScope, onScopeSelected = {newScope = it})
                Button(onClick = {
                    store.dispatch(SubmitRescheduleTask(task, newScope))
                }){
                    Text("Submit")
                }
            }
        }
    }
}