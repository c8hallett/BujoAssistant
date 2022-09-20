import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.hallett.corndux.IState
import com.hallett.corndux.Store


lateinit var LocalStore: ProvidableCompositionLocal<Store<out IState>>

@Composable
inline fun WithGlobalStore(
    globalStore: Store<out IState>,
    crossinline operation: @Composable () -> Unit
) {
    LocalStore = compositionLocalOf { globalStore }
    CompositionLocalProvider(LocalStore provides globalStore) {
        operation()
    }
}

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