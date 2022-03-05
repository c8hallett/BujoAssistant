import androidx.compose.runtime.Composable
import com.hallett.taskassistant.corndux.IStore
import com.hallett.taskassistant.taskdashboard.corndux.IDashboardStore
import org.kodein.di.DIProperty
import org.kodein.di.compose.rememberInstance

@Composable
fun taskAssistantStore(): DIProperty<IStore> = rememberInstance<IStore>()

@Composable
fun dashboardStore(): DIProperty<IDashboardStore> = rememberInstance<IDashboardStore>()