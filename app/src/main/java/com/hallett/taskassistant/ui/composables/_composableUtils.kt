import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.ui.viewmodels.OverdueTaskViewModel
import com.hallett.taskassistant.ui.viewmodels.TaskListViewModel
import org.kodein.di.DI
import org.kodein.di.DIProperty
import org.kodein.di.compose.rememberInstance
import org.kodein.di.instance

@Composable
fun taskAssistantStore(): DIProperty<IStore> = rememberInstance<IStore>()

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
