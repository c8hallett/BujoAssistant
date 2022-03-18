import androidx.compose.runtime.Composable
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import org.kodein.di.DIProperty
import org.kodein.di.compose.rememberInstance

@Composable
fun closestStore(): DIProperty<Store<out IState>> = rememberInstance()