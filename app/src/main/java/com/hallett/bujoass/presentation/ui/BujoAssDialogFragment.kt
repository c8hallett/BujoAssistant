package com.hallett.bujoass.presentation.ui

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.hallett.bujoass.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

open class BujoAssDialogFragment: DialogFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    protected val vmpfactory: ViewModelProvider.Factory by instance()

    open val isFullScreen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isFullScreen) {
            setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen)
        } else {
            setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog)
        }
    }
}