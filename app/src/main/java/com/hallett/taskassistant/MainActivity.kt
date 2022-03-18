package com.hallett.taskassistant

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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hallett.database.di.databaseModule
import com.hallett.scopes.di.scopeGeneratorModule
import com.hallett.taskassistant.corndux.cornduxModule
import com.hallett.taskassistant.mainNavigation.MainNavHost
import com.hallett.taskassistant.mainNavigation.TaskBottomAppBar
import com.hallett.taskassistant.mainNavigation.TaskFloatingActionBar
import com.hallett.taskassistant.ui.composables.ScopeTypeDropDownMenu
import com.hallett.taskassistant.ui.theme.TaskAssistantTheme
import com.hallett.taskassistant.util.AndroidLoggerHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.kodein.di.DI
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bindProvider
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
            bindProvider<NavController> { navController }
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
            Scaffold(
                bottomBar = { TaskBottomAppBar() },
                floatingActionButton = { TaskFloatingActionBar() },
            ) { innerPadding ->
                MainNavHost(innerPadding = innerPadding, navController = navController)
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
