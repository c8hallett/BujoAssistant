import androidx.compose.runtime.Composable
import com.hallett.taskassistant.corndux.IStore
import org.kodein.di.DIProperty
import org.kodein.di.compose.rememberInstance

@Composable
fun taskAssistantStore(): DIProperty<IStore> = rememberInstance<IStore>()