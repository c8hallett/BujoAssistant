package com.hallett.taskassistant

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.hallett.scopes.model.Scope
import com.hallett.taskassistant.ui.TaskAssistantViewModel
import com.hallett.taskassistant.ui.composables.ScopeSelectorScreen
import com.hallett.taskassistant.ui.composables.ScopeTypeSelector
import com.hallett.taskassistant.ui.composables.TaskEditScreen
import com.hallett.taskassistant.ui.theme.TaskAssistantTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : ComponentActivity(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val vmpfactory: ViewModelProvider.Factory by instance()

    private val viewModel: TaskAssistantViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(TaskAssistantViewModel::class.java)
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskAssistantTheme {
                val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
                val coroutineScope = rememberCoroutineScope()
                val (selectedScope, setSelectedScope) = remember{ mutableStateOf<Scope?>(null) }
                ScopeSelectorScreen(
                    scopes = viewModel.observeScopes(),
                    modalState = modalState,
                    onScopeTypeSelected = viewModel::onNewScopeTypeSelected,
                    onScopeSelected = { newScope ->
                        setSelectedScope(newScope)
                        coroutineScope.launch {
                            modalState.hide()
                        }
                    },
                    onFullyExpandedContent = {
                        Text("Hello!")
                    }
                ) {
                    TaskEditScreen(
                        scope = selectedScope,
                        onTaskSubmitted = { Toast.makeText(baseContext, "New task $it", Toast.LENGTH_LONG).show() },
                        onScopeClicked = {
                            coroutineScope.launch { modalState.show() }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(true) }
    TaskAssistantTheme {
        ScopeTypeSelector(
            isExpanded = isExpanded,
            onDismiss = { setIsExpanded(!isExpanded) },
            onScopeTypeSelected = { }
        )
    }
}