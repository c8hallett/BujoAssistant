package com.hallett.bujoass.presentation.ui.task_list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hallett.bujoass.presentation.model.Task
import timber.log.Timber

class TaskSwipeHelper(private val callbacks: SwipeCallbacks):
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    interface SwipeCallbacks {
        fun canPositionBeSwiped(position: Int): Boolean
        fun getTaskAtPosition(position: Int): Task
        fun onTaskSwipe(task: Task, swipe: Swipe)
    }

    enum class Swipe {
        LEFT,
        RIGHT
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when(direction) {
            ItemTouchHelper.RIGHT -> callbacks.onTaskSwipe(callbacks.getTaskAtPosition(viewHolder.adapterPosition), Swipe.RIGHT)
            ItemTouchHelper.LEFT -> callbacks.onTaskSwipe(callbacks.getTaskAtPosition(viewHolder.adapterPosition), Swipe.LEFT)
            else -> Timber.i("Received swipe for direction $direction (${viewHolder.adapterPosition})")
        }
    }



    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return when {
            callbacks.canPositionBeSwiped(viewHolder.adapterPosition) -> ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            else -> 0
        }
    }
}