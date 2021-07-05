package com.hallett.bujoass.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

open class BujoAssFragment: Fragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    protected val vmpfactory: ViewModelProvider.Factory by instance()

    protected val keyOperationMap: MutableMap<String, SavedStateHandle.() -> Unit> = mutableMapOf()
    private val stateHandleObserver: LifecycleEventObserver = LifecycleEventObserver{ _, event ->
        Timber.i("Backstack lifecycle event: $event")
        when(event) {
            Lifecycle.Event.ON_RESUME -> findNavController().currentBackStackEntry?.savedStateHandle?.run {
                Timber.i("Current values: $this")
                keyOperationMap.forEach {
                    it.value.invoke(this)
                }
            }
            else -> {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("View created")
        viewLifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                // automatically hooks up the backstack listener with the fragment's lifecycle
                findNavController().currentBackStackEntry?.let {
                    when (event) {
                        Lifecycle.Event.ON_START -> {
                            Timber.i("Adding backstackentry listener")
                            it.lifecycle.addObserver(stateHandleObserver)
                        }
                        Lifecycle.Event.ON_DESTROY -> {
                            Timber.i("Removing backstackentry listener")
                            it.lifecycle.removeObserver(stateHandleObserver)
                        }
                        else -> {}
                    }
                }
            }
        )
    }

    protected fun <T> NavController.setNavigationResult(key: String, value: T) {
        Timber.i("Passing ($key = $value) back to previous destination")
        previousBackStackEntry
            ?.savedStateHandle
            ?.set(key, value)
    }
    protected fun removeNavigationResultListener(key: String) {
        Timber.i("Removing navigation result listener")
        keyOperationMap.remove(key)
    }

    protected inline fun <reified D : BujoAssDialogFragment> removeDialogResult() {
        removeNavigationResultListener(D::class.java.simpleName)
    }

    protected inline fun <T> getNavigationResult(key: String, crossinline onResult: (result: T) -> Unit) {
        Timber.i("Setting up navigation result listener ($key)")
        keyOperationMap[key] = {
            if (contains(key)) { // checks if key is present in savedStateHandle
                get<T>(key)?.let { onResult(it) } // performs operation on value, if present
                remove<T>(key) // removes key from savedState after it has been used
            }
        }
    }

    protected inline fun <reified D : BujoAssDialogFragment> getDialogResult(crossinline onResult: (result: Boolean) -> Unit) {
        getNavigationResult(D::class.java.simpleName, onResult)
    }
}