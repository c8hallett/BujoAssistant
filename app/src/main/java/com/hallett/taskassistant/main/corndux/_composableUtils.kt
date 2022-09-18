import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.taskassistant.LocalStore

@Composable
inline fun WithStore(localStore: Store<out IState>, crossinline operation: @Composable () -> Unit) {
    val globalStore = LocalStore.current
    DisposableEffect(globalStore) {
        globalStore.observe(localStore)
        onDispose { globalStore.stopObserving(localStore) }
    }
    CompositionLocalProvider(LocalStore provides localStore) {
        operation()
    }
}