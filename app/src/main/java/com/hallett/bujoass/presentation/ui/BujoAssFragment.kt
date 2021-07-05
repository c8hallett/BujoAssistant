package com.hallett.bujoass.presentation.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

open class BujoAssFragment: Fragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    protected val vmpfactory: ViewModelProvider.Factory by instance()

    fun <T> NavController.setNavigationResult(key: String, value: T) {
        previousBackStackEntry?.savedStateHandle?.set(
            key,
            value
        )
    }

    inline fun <T> NavController.getNavigationResult(key: String, crossinline onResult: (result: T) -> Unit) {
        Timber.i("Setting up getting navigation result")
        currentBackStackEntry?.let { currentEntry ->
            // creating listener for key
            val observer = LifecycleEventObserver { _, event ->
                Timber.i("Received lifecycle event: $event. statehandle = ${currentEntry.savedStateHandle.keys()}")
                when (event) {
                    // checks for key on every on resume
                    Lifecycle.Event.ON_RESUME -> currentEntry.savedStateHandle.run {
                        if (contains(key)) {
                            get<T>(key)?.let { onResult(it) }
                            remove<T>(key) // removes key after it has been used
                        }
                    }
                    else -> {}
                }
            }
            // adding listener for key
            currentEntry.lifecycle.addObserver(observer)

            // removing listener for key when fragment is destroyed
            viewLifecycleOwner.lifecycle.addObserver(
                LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_DESTROY -> currentEntry.lifecycle.removeObserver(observer)
                        else -> {}
                    }
                }
            )
        } ?: Timber.w("Current backstack entry was null")
    }

    inline fun <T, reified D : BujoAssDialogFragment> NavController.getDialogResult(crossinline onResult: (result: T) -> Unit) {
        getNavigationResult(D::class.java.simpleName, onResult)
    }

}