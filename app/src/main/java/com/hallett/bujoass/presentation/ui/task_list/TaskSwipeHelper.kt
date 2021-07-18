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

class TaskSwipeHelper(private val scope: CoroutineScope,  private val callbacks: SwipeCallbacks):
    ItemTouchHelper.Callback() {

    private val defaultIconMargin = callbacks.getContext().resources.getDimensionPixelSize(R.dimen.dp12)
    private val longPressTimeout = 500L

    private var currentSwipe: Swipe? = null
    private var longHoldJob: Job? = null

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
        HOLD_LEFT,
        HOLD_RIGHT,
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        
        when(val swipe = currentSwipe) {
            null -> Timber.i("Received swipe for direction $direction (${viewHolder.adapterPosition})")
            else -> callbacks.onTaskSwipe(callbacks.getTaskAtPosition(viewHolder.adapterPosition), swipe)
        }
        currentSwipe = null
        resetJob()
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
            dX > 0 -> {
                when(currentSwipe) {
                    Swipe.RIGHT, Swipe.HOLD_RIGHT -> {} // nothing to do
                    else -> {
                        resetJob()
                        currentSwipe = Swipe.RIGHT
                        longHoldJob = scope.launch {
                            delay(longPressTimeout)
                            currentSwipe = Swipe.HOLD_RIGHT
                        }
                    }
                }
            }
            // left swipe
            dX < 0 ->{
                when(currentSwipe) {
                    Swipe.LEFT, Swipe.HOLD_LEFT -> {}
                    else -> {
                        resetJob()
                        currentSwipe = Swipe.LEFT
                        longHoldJob = scope.launch {
                            delay(longPressTimeout)
                            currentSwipe = Swipe.HOLD_LEFT
                        }
                    }
                }
            }
            else -> {
                resetJob()
                currentSwipe = null
            }
        }
        when(currentSwipe) {
            null -> return
            Swipe.RIGHT -> completeDrawable?.setBoundsForRightSwipe(itemView)
            Swipe.HOLD_RIGHT -> deleteDrawable?.setBoundsForRightSwipe(itemView)
            Swipe.LEFT -> deferDrawable?.setBoundsForLeftSwipe(itemView)
            Swipe.HOLD_LEFT -> rescheduleDrawable?.setBoundsForLeftSwipe(itemView)
        }?.draw(c)
    }

    private val completeDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_check_round)
    private val deleteDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_remove_round)
    private val deferDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_arrow)
    private val rescheduleDrawable = ContextCompat.getDrawable(callbacks.getContext(), R.drawable.ic_double_arrow)

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


    private fun resetJob(){
        longHoldJob?.cancel()
        longHoldJob = null
    }
}