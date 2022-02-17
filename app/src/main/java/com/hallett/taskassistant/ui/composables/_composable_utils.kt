import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.hallett.taskassistant.ui.viewmodels.ScopeSelectionViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskEditViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskListViewModel
import org.kodein.di.DI
import org.kodein.di.instance

fun DI.scopeSelectionViewModel(): ScopeSelectionViewModel {
    val viewModelStoreOwner: ViewModelStoreOwner by instance()
    val vmpfactory: ViewModelProvider.Factory by instance()
    return ViewModelProvider(viewModelStoreOwner, vmpfactory)
        .get(ScopeSelectionViewModel::class.java)
}

fun DI.taskEditViewModel(): TaskEditViewModel {
    val viewModelStoreOwner: ViewModelStoreOwner by instance()
    val vmpfactory: ViewModelProvider.Factory by instance()
    return ViewModelProvider(viewModelStoreOwner, vmpfactory)
        .get(TaskEditViewModel::class.java)
}

fun DI.taskListViewModel(): TaskListViewModel {
    val viewModelStoreOwner: ViewModelStoreOwner by instance()
    val vmpfactory: ViewModelProvider.Factory by instance()
    return ViewModelProvider(viewModelStoreOwner, vmpfactory)
        .get(TaskListViewModel::class.java)
}