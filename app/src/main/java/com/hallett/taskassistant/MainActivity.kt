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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.hallett.taskassistant.ui.TaskAssistantViewModel
import com.hallett.taskassistant.ui.composables.ScopeSelector
import com.hallett.taskassistant.ui.composables.ScopeTypeSelector
import com.hallett.taskassistant.ui.theme.TaskAssistantTheme
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

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
                val scope = rememberCoroutineScope()
                ScopeSelector(
                    scopes = viewModel.observeScopes(),
                    modalState = modalState,
                    onScopeTypeSelected = viewModel::onNewScopeTypeSelected,
                    onScopeSelected = { newScope ->
                        Toast.makeText(
                            this@MainActivity,
                            "New Scope selected: $newScope",
                            Toast.LENGTH_SHORT
                        ).show()
                        scope.launch {
                            modalState.hide()
                        }
                    }
                ) {
                    Button(onClick = {

                        scope.launch { modalState.show() }
                    }) {
                        Text("\"Click to sleect new state\"")
                    }
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