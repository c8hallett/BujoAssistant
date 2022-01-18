package com.hallett.taskassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.hallett.scopes.di.scopeGeneratorModule
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.di.databaseModule
import com.hallett.taskassistant.di.formatterModule
import com.hallett.taskassistant.di.pagingModule
import com.hallett.taskassistant.di.viewModelModule
import com.hallett.taskassistant.ui.TaskAssistantViewModel
import com.hallett.taskassistant.ui.composables.ScopeSelectorScreen
import com.hallett.taskassistant.ui.composables.ScopeTypeDropDownMenu
import com.hallett.taskassistant.ui.composables.TaskEditScreen
import com.hallett.taskassistant.ui.theme.TaskAssistantTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.compose.withDI
import org.kodein.di.instance

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@FlowPreview
class MainActivity : ComponentActivity(), DIAware {
    override val di: DI by DI.lazy {
        importAll(
            viewModelModule,
            databaseModule,
            formatterModule,
            pagingModule,
            scopeGeneratorModule,
            androidXModule(application)
        )
    }

    private val vmpfactory: ViewModelProvider.Factory by instance()
    private val viewModel: TaskAssistantViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(TaskAssistantViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskAssistantTheme { App() }
        }
    }

    @Composable
    fun App() = withDI(di) {
        val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val coroutineScope = rememberCoroutineScope()
        val selectedScope by viewModel.observeSelectedScope().collectAsState(initial = null)
        val taskName by viewModel.getTaskName().collectAsState(initial = "")
        val selectedScopeType by viewModel.observeScopeType().collectAsState(initial = ScopeType.DAY)

        ScopeSelectorScreen(
            scopeList = viewModel.observeScopeSelectorList(),
            scopeType = selectedScopeType,
            modalState = modalState,
            onScopeTypeSelected = viewModel::onNewScopeTypeSelected,
            onScopeSelected = { newScope ->
                viewModel.setTaskScope(newScope)
                coroutineScope.launch {
                    modalState.hide()
                }
            },
        ) { 
            TaskEditScreen(
                taskName = taskName,
                scope = selectedScope,
                onTaskNameUpdated = viewModel::setTaskName,
                onTaskSubmitted = viewModel::onTaskSubmitted,   
                onScopeClicked = {
                    coroutineScope.launch { modalState.show() }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(true) }
    TaskAssistantTheme {
        ScopeTypeDropDownMenu(
            isExpanded = isExpanded,
            onDismiss = { setIsExpanded(!isExpanded) },
            onScopeTypeSelected = { }
        )
    }
}