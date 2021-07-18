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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.min

class TaskSwipeHelper(private val callbacks: SwipeCallbacks):
    ItemTouchHelper.Callback() {

    private val defaultIconMargin = callbacks.getContext().resources.getDimensionPixelSize(R.dimen.dp12)

    interface SwipeCallbacks {
        fun getContext(): Context
        // fun getDrawableFor(position: Int, swipe: Swipe): Drawable
        fun canPositionBeSwiped(position: Int): Boolean
        fun getTaskAtPosition(position: Int): Task
        fun onTaskSwipe(task: Task, swipe: Swipe)
    }

    enum class Swipe {
        LEFT,
        RIGHT,
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when(direction){
            ItemTouchHelper.LEFT -> callbacks.onTaskSwipe(callbacks.getTaskAtPosition(viewHolder.adapterPosition), Swipe.LEFT)
            ItemTouchHelper.RIGHT -> callbacks.onTaskSwipe(callbacks.getTaskAtPosition(viewHolder.adapterPosition), Swipe.RIGHT)
        }
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = when {
            callbacks.canPositionBeSwiped(viewHolder.adapterPosition) -> ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            else -> 0
        }
        return makeMovementFlags(0, swipeFlags)
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
            dX > 0 -> completeDrawable?.setBoundsForRightSwipe(itemView)
            // left swipe
            dX < 0 -> deferDrawable?.setBoundsForLeftSwipe(itemView)
            else -> null
        }?.draw(c)
    }

    private val completeDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_check_round)
    private val deferDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_arrow)
//    private val deleteDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_remove_round)
//    private val rescheduleDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_double_arrow)

    private fun Drawable.setBoundsForRightSwipe(itemView: View): Drawable = apply {
        val iconSize = min(intrinsicHeight, itemView.height - defaultIconMargin * 2)
        val iconMargin: Int = (itemView.height - iconSize) / 2

        val iconTop: Int = itemView.top + iconMargin
        val iconBottom: Int = iconTop + iconSize

        val iconLeft = itemView.left + defaultIconMargin
        val iconRight = iconLeft + iconSize

        setBounds(iconLeft, iconTop, iconRight, iconBottom)
    }

    private fun Drawable.setBoundsForLeftSwipe(itemView: View): Drawable = apply {
        val iconSize = min(intrinsicHeight, itemView.height - defaultIconMargin * 2)
        val iconMargin: Int = (itemView.height - iconSize) / 2

        val iconTop: Int = itemView.top + iconMargin
        val iconBottom: Int = iconTop + iconSize

        val iconRight = itemView.right - defaultIconMargin
        val iconLeft = iconRight - iconSize

        setBounds(iconLeft, iconTop, iconRight, iconBottom)
    }
}