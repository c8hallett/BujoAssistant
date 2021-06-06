package com.hallett.bujoass.di

import androidx.lifecycle.ViewModel
import com.hallett.bujoass.presentation.viewmodel.NewFragmentViewModel
import com.hallett.bujoass.presentation.viewmodel.TaskListFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val viewModelModule = Kodein.Module("view_model_module") {
    bind<ViewModel>(tag = NewFragmentViewModel::class.java) with singleton { NewFragmentViewModel(instance()) }
    bind<ViewModel>(tag = TaskListFragmentViewModel::class.java) with singleton { TaskListFragmentViewModel(instance()) }
}