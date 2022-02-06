package com.hallett.taskassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hallett.scopes.di.scopeGeneratorModule
import com.hallett.taskassistant.di.databaseModule
import com.hallett.taskassistant.di.formatterModule
import com.hallett.taskassistant.di.pagingModule
import com.hallett.taskassistant.di.viewModelModule
import com.hallett.taskassistant.domain.Task
import com.hallett.taskassistant.ui.viewmodels.TaskEditViewModel
import com.hallett.taskassistant.ui.composables.ScopeTypeDropDownMenu
import com.hallett.taskassistant.ui.composables.TaskEdit
import com.hallett.taskassistant.ui.composables.TaskList
import com.hallett.taskassistant.ui.theme.TaskAssistantTheme
import com.hallett.taskassistant.ui.viewmodels.ScopeSelectionViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
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
    private val scopeSelectionVm: ScopeSelectionViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(ScopeSelectionViewModel::class.java)
    }
    private val taskEditVm: TaskEditViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(TaskEditViewModel::class.java)
    }
    private val taskListVm: TaskListViewModel by lazy {
        ViewModelProvider(this, vmpfactory).get(TaskListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskAssistantTheme {
                App()
            }
        }
    }

    @Composable
    fun App() {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { BottomAppBar { Text("it's a me, a bottom app bar") } },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("taskEdit/${Task.DEFAULT_VALUE.id}") }) {
                    Icon(Icons.Default.Add, "new task")
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "list",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    "taskEdit/{taskId}",
                    arguments = listOf(navArgument("taskId"){
                        type = NavType.LongType
                        defaultValue = Task.DEFAULT_VALUE.id
                    })
                ) { backStackEntry ->
                    TaskEdit(
                        taskEditVm = taskEditVm,
                        scopeSelectionVm = scopeSelectionVm,
                        di = di,
                        navController = navController,
                        taskId =  backStackEntry.arguments?.getLong("taskId") ?: Task.DEFAULT_VALUE.id
                    )
                }
                composable("list") {
                    TaskList(
                        taskEditVm = taskListVm,
                        scopeSelectionVm = scopeSelectionVm,
                        di = di,
                        navController = navController
                    )
                }
            }
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