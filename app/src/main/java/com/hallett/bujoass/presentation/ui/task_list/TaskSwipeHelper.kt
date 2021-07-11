package com.hallett.bujoass.presentation.ui.task_list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class TaskSwipeHelper(private val onSwipe: (Int, Swipe) -> Unit):
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

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
            ItemTouchHelper.RIGHT -> onSwipe(viewHolder.adapterPosition, Swipe.RIGHT)
            ItemTouchHelper.LEFT -> onSwipe(viewHolder.adapterPosition, Swipe.LEFT)
            else -> Timber.i("Received swipe for direction $direction (${viewHolder.adapterPosition})")
        }
    }
}