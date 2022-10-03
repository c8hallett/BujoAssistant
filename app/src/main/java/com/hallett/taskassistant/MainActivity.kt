package com.hallett.taskassistant

import WithGlobalStore
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.hallett.corndux.Store
import com.hallett.database.di.databaseModule
import com.hallett.scopes.di.scopeGeneratorModule
import com.hallett.taskassistant.features.scopeSelection.ScopeTypeDropDownMenu
import com.hallett.taskassistant.main.MainNavHost
import com.hallett.taskassistant.main.TaskBottomAppBar
import com.hallett.taskassistant.main.TaskFloatingActionBar
import com.hallett.taskassistant.main.corndux.GlobalState
import com.hallett.taskassistant.main.di.cornduxModule
import com.hallett.taskassistant.main.di.formatterModule
import com.hallett.taskassistant.main.di.pagingModule
import com.hallett.taskassistant.main.di.utilModule
import com.hallett.taskassistant.ui.theme.TaskAssistantTheme
import com.hallett.taskassistant.util.AndroidLoggerHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.kodein.di.DI
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bindProvider
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.withDI


@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@FlowPreview
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidLoggerHandler.setup()
        setContent {
            TaskAssistantTheme {
                App()
            }
        }
    }

    @Composable
    fun App() {
        val navController = rememberNavController()
        val runtimeModule = DI.Module("runtime_module") {
            bindProvider<CoroutineScope> { lifecycleScope }
        }
        withDI(
            databaseModule,
            formatterModule,
            pagingModule,
            scopeGeneratorModule,
            utilModule,
            cornduxModule,
            androidXModule(application),
            runtimeModule
        ) {
            val globalStore by rememberInstance<Store<GlobalState>>()
            WithGlobalStore(globalStore) {
                Scaffold(
                    bottomBar = { TaskBottomAppBar(navController) },
                    floatingActionButton = { TaskFloatingActionBar() },
                ) { innerPadding ->
                    MainNavHost(innerPadding = innerPadding, navController = navController)
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