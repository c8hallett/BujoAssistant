import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.TaskAssistantAction
import com.hallett.taskassistant.corndux.TaskAssistantSideEffect
import com.hallett.taskassistant.corndux.TaskAssistantState
import com.hallett.taskassistant.ui.viewmodels.OverdueTaskViewModel
import com.hallett.taskassistant.ui.viewmodels.ScopeSelectionViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskEditViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskListViewModel
import org.kodein.di.DI
import org.kodein.di.DIProperty
import org.kodein.di.compose.rememberInstance
import org.kodein.di.instance

@Composable
fun taskAssistantStore(): DIProperty<TaskStore> = rememberInstance<TaskStore>()

fun DI.taskListViewModel(): TaskListViewModel {
    val viewModelStoreOwner: ViewModelStoreOwner by instance()
    val vmpfactory: ViewModelProvider.Factory by instance()
    return ViewModelProvider(viewModelStoreOwner, vmpfactory)
        .get(TaskListViewModel::class.java)
}

fun DI.overviewTaskViewModel(): OverdueTaskViewModel {
    val viewModelStoreOwner: ViewModelStoreOwner by instance()
    val vmpfactory: ViewModelProvider.Factory by instance()
    return ViewModelProvider(viewModelStoreOwner, vmpfactory)
        .get(OverdueTaskViewModel::class.java)
}

typealias TaskStore = Store<TaskAssistantState, TaskAssistantAction, TaskAssistantSideEffect>