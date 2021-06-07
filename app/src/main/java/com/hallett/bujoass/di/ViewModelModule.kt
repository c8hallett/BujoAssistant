package com.hallett.bujoass.di

import androidx.lifecycle.ViewModel
import com.hallett.bujoass.presentation.ui.new_task.NewFragmentViewModel
import com.hallett.bujoass.presentation.ui.task_list.TaskListFragmentViewModel
import com.hallett.bujoass.presentation.ui.view_task.ViewTaskDialogFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

val viewModelModule = Kodein.Module("view_model_module") {
    bind<ViewModel>(tag = NewFragmentViewModel::class.java) with provider { NewFragmentViewModel(instance()) }
    bind<ViewModel>(tag = TaskListFragmentViewModel::class.java) with singleton { TaskListFragmentViewModel(instance()) }
    bind<ViewModel>(tag = ViewTaskDialogFragmentViewModel::class.java) with provider { ViewTaskDialogFragmentViewModel(instance(), instance()) }
}