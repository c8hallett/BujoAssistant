package com.hallett.taskassistant.ui.rescheduleDialog

import LocalStore
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    Dialog(onDismissRequest = { store.dispatch(CancelRescheduleTask) }) {
        Column {
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