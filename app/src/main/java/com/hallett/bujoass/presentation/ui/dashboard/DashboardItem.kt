package com.hallett.bujoass.presentation.ui.dashboard

import com.hallett.bujoass.presentation.model.Task

sealed class DashboardItem{
    class TaskItem(val task: Task): DashboardItem()
    class HeaderItem(val headerText: String): DashboardItem()
}
