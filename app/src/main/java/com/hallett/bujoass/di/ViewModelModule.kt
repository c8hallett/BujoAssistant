package com.hallett.bujoass.di

import androidx.lifecycle.ViewModel
import com.hallett.bujoass.presentation.ui.new_task.AddNewTaskFragmentViewModel
import com.hallett.bujoass.presentation.ui.task_list.TaskListFragmentViewModel
import com.hallett.bujoass.presentation.ui.view_task.ViewTaskFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

val viewModelModule = Kodein.Module("view_model_module") {
    bind<ViewModel>(tag = AddNewTaskFragmentViewModel::class.java) with provider { AddNewTaskFragmentViewModel(instance()) }
    bind<ViewModel>(tag = TaskListFragmentViewModel::class.java) with singleton { TaskListFragmentViewModel(instance(), instance(), instance(), instance(), instance()) }
    bind<ViewModel>(tag = ViewTaskFragmentViewModel::class.java) with provider { ViewTaskFragmentViewModel(instance(), instance(), instance(), instance(), instance(), instance()) }
}