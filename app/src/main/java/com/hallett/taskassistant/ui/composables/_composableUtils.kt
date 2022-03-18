import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.taskassistant.corndux.IInterpreter
import com.hallett.taskassistant.corndux.IStore
import org.kodein.di.DIProperty
import org.kodein.di.compose.rememberInstance

@Composable
fun closestStore(): DIProperty<Store<out IState>> = rememberInstance()

@Composable
fun taskAssistantInterpreter(): DIProperty<IInterpreter> = rememberInstance()