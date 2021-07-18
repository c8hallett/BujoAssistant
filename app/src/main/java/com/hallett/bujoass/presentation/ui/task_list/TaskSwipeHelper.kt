package com.hallett.bujoass.presentation.ui.task_list

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hallett.bujoass.R
import com.hallett.bujoass.presentation.model.Task
import timber.log.Timber

class TaskSwipeHelper(private val callbacks: SwipeCallbacks):
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val completeDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_check_round)
    private val deferDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_arrow)

    interface SwipeCallbacks {
        fun getContext(): Context
        fun canPositionBeSwiped(position: Int): Boolean
        fun getTaskAtPosition(position: Int): Task
//        fun getIconForSwipe(swipe: Swipe): Drawable
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

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        when {
            // right swipe
            dX > 0 -> completeDrawable?.run{
                setBoundsForRightSwipe(itemView)
                draw(c)
            }
            // left swipe
            dX < 0 -> deferDrawable?.run{
                setBoundsForLeftSwipe(itemView)
                draw(c)
            }
        }
    }

    private fun Drawable.setBoundsForRightSwipe(itemView: View) {
        val iconMargin: Int = (itemView.height - intrinsicHeight) / 2
        val iconLeft = itemView.left + iconMargin
        val iconTop: Int = itemView.top + iconMargin
        val iconRight = iconLeft + intrinsicWidth
        val iconBottom: Int = iconTop + intrinsicHeight

        setBounds(iconLeft, iconTop, iconRight, iconBottom)
    }

    private fun Drawable.setBoundsForLeftSwipe(itemView: View) {

        val iconMargin: Int = (itemView.height - intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - intrinsicWidth
        val iconTop: Int = itemView.top + iconMargin
        val iconBottom: Int = iconTop + intrinsicHeight
        val iconRight = iconLeft + intrinsicWidth

        setBounds(iconLeft, iconTop, iconRight, iconBottom)
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