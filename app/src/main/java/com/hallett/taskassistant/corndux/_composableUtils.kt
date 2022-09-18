import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import com.hallett.corndux.IState
import com.hallett.corndux.Store
import com.hallett.logging.logD
import com.hallett.taskassistant.LocalStore

@Composable
inline fun WithStore(localStore: Store<out IState>, crossinline operation: @Composable () -> Unit) {
    val globalStore = LocalStore.current
    logD("DEBUG_STORE", ".\n${globalStore::class.simpleName}\n👆 ${localStore::class.simpleName}")
    DisposableEffect(globalStore) {
        globalStore.prepend(localStore)
        onDispose { globalStore.remove(localStore) }
    }
    CompositionLocalProvider(LocalStore provides localStore) {
        operation()
    }
}