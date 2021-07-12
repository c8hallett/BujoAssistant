package com.hallett.bujoass.presentation.ui.dashboard

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber


class StickyHeaderDecoration(private val getter: StickyHeaderGetter): RecyclerView.ItemDecoration() {
    private var stickyHeaderHeight: Int = 0


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val firstVisibleChild = parent.getChildAt(0) ?: return
        val firstVisiblePosition = parent.getChildAdapterPosition(firstVisibleChild)
        if(firstVisiblePosition == RecyclerView.NO_POSITION) return // child not found in adapter, ignore

        val (headerPosition, currentHeader) = getter.getCurrentHeader(firstVisiblePosition) ?: return // no header found, ignore
        currentHeader.fixLayoutSize(parent)

        val headerHeight = with(currentHeader) { bottom } //- marginBottom
        when(val overlappedView = getOverlappedView(parent, headerHeight)) {
            null -> return
            else -> {
                val overlappedPosition = parent.getChildAdapterPosition(overlappedView)
                when{
                    overlappedPosition == headerPosition -> return
                    getter.isHeader(overlappedPosition) -> c.moveHeader(currentHeader, overlappedView, parent.top)
                    else -> c.drawHeader(currentHeader, parent.top.toFloat())
                }
            }
        }
    }

    private fun Canvas.drawHeader(header: View, paddingTop: Float) {
        save()
        // dx = marginLeft, dy += marginTop
        translate(0f, paddingTop)
        header.draw(this)
        restore()
    }

    private fun Canvas.moveHeader(currentHeader: View, nextHeader: View, paddingTop: Int) {
        save()
        // dx = marginLeft
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
        contactPoint: Int,
    ): View? {
        // go through each visible child
        repeat(parent.childCount) { index ->
            val child = parent.getChildAt(index)
            val bounds = Rect().apply { parent.getDecoratedBoundsWithMargins(child, this) }

            // if bottom of header is in the middle of view, then it is the overlapped view
            /*   ______________________________  top of header
                 |                            |
                 |                            |
                ||============================|| top of child
                ||         (overlap)          ||
                |------------------------------| bottom of header (contact point)
                |                              |
                |==============================| bottom of child */
            if (bounds.bottom > contactPoint && bounds.top <= contactPoint) {
                // This child overlaps the contactPoint
                return child
            }
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