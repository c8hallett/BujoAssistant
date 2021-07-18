package com.hallett.bujoass.presentation.ui

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hallett.bujoass.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

open class BujoAssDialogFragment: DialogFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    protected val vmpfactory: ViewModelProvider.Factory by instance()

    sealed class DialogType() {
        object FullScreen: DialogType()
        class Floating(val dimBehind: Boolean): DialogType()
    }
    open val dialogType: DialogType = DialogType.FullScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(dialogType) {
            is DialogType.FullScreen -> setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen)
            is DialogType.Floating -> setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(val type = dialogType) {
            is DialogType.Floating ->dialog?.window?.run {

                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawableResource(android.R.color.transparent)
                when{
                    type.dimBehind -> {
                        addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        attributes = attributes.apply {
                            dimAmount = 0.25f
                        }
                    }
                    else -> {
                        clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        attributes = attributes.apply {
                            dimAmount = 0.0f
                        }
                    }
                }
            }

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        notifyOnDismissed()
        super.onDismiss(dialog)
    }

    open fun notifyOnDismissed() {
        onCompleted(true)
    }

    protected fun <T> NavController.setNavigationResult(key: String, value: T) {
        previousBackStackEntry?.savedStateHandle?.set(
            key,
            value
        )
    }

    protected fun onCompleted(completed: Boolean) {
        findNavController().setNavigationResult(this::class.java.simpleName, completed)
    }
}