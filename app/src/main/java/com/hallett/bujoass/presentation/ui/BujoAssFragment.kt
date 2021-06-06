package com.hallett.bujoass.presentation.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

open class BujoAssFragment: Fragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    protected val vmpfactory: ViewModelProvider.Factory by instance()

}