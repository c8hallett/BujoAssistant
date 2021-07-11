package com.hallett.bujoass.presentation.ui.dashboard

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class StickyHeaderDecoration(private val getter: StickyHeaderGetter): RecyclerView.ItemDecoration() {
    private var stickyHeaderHeight: Int = 0

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val firstVisibleChild = parent.getChildAt(0) ?: return
        val firstVisiblePosition = parent.getChildAdapterPosition(firstVisibleChild)
        if(firstVisiblePosition == RecyclerView.NO_POSITION) return

        val (headerPos, currentHeader) = getter.getCurrentHeader(firstVisiblePosition) ?: return
        currentHeader.fixLayoutSize(parent)
        val overlappedView = getOverlappedView(parent, currentHeader.bottom, headerPos)
        when {
            overlappedView == null -> c.drawHeader(currentHeader)
            getter.isHeader(parent.getChildAdapterPosition(overlappedView)) -> c.moveHeader(currentHeader, overlappedView)
            else -> c.drawHeader(currentHeader)
        }
    }

    private fun Canvas.drawHeader(header: View) {
        save()
        translate(0f, 0f)
        header.draw(this)
        restore()
    }

    private fun Canvas.moveHeader(currentHeader: View, nextHeader: View) {
        save()
        // next header pushes currentHeader upwards
        translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        currentHeader.draw(this)
        restore()
    }

    private fun View.fixLayoutSize(parent: ViewGroup) {


        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            layoutParams.height
        )

        measure(childWidthSpec, childHeightSpec)
        stickyHeaderHeight = measuredHeight
        layout(0, 0, measuredWidth, measuredHeight)
    }

    private fun getOverlappedView(
        parent: RecyclerView,
        headerBottom: Int,
        currentHeaderPos: Int
    ): View? {
        // go through each visible child
        repeat(parent.childCount) { index ->
            val child = parent.getChildAt(index)
            val childAdapterPosition = parent.getChildAdapterPosition(child)

            /*
           If child view is a header (which is not the sticky header),
           it should be shifted to be below the sticky header.
           As the child view scrolls up/off screen,
           - the height becomes smaller
           - headerOffset becomes larger
           - more the view is adjusted downwards to accommodate the sticky header
             */
            val headerOffset = when {
                currentHeaderPos != index && getter.isHeader(childAdapterPosition) -> stickyHeaderHeight - child.height
                else -> 0
            }

            val childBottomPosition = when {
                // if top of view is being displayed, should be sifted by offset
                // otherwise, let it scroll off screen
                child.top > 0 -> child.bottom + headerOffset
                else -> child.bottom
            }

            // if bottom of header is in the middle of view, then it is the overlapped view
            /*   ______________________________  top of header
                 |                            |
                 |                            |
                ||============================|| top of child
                ||         (overlap)          ||
                |------------------------------| bottom of header
                |                              |
                |==============================| bottom of child */
            if(childBottomPosition > headerBottom && child.top <= headerBottom) return child
        }

        return null
    }

    interface StickyHeaderGetter {
        /**
         * Method returns the most recent header at the given position.
         */
        fun getCurrentHeader(position: Int): Pair<Int, View>?

        fun isHeader(position: Int): Boolean
    }
}