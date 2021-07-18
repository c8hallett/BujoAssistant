package com.hallett.bujoass.presentation.ui.dashboard

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.ListItemHeaderBinding
import com.hallett.bujoass.databinding.ListItemTaskBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.task_list.TaskSwipeHelper
import java.lang.ref.WeakReference

class DashboardAdapter(
    private val onTaskClicked: (Task) -> Unit,
    private val onTaskSwiped: (Task, TaskSwipeHelper.Swipe) -> Unit,
): RecyclerView.Adapter<DashboardAdapter.ViewHolder>(),
    StickyHeaderDecoration.StickyHeaderGetter,
    TaskSwipeHelper.SwipeCallbacks
{

    private lateinit var contextRef: WeakReference<Context>
    private var itemList: List<DashboardItem> = listOf()

    fun setItems(newItems: List<DashboardItem>) {
        itemList = newItems
        notifyDataSetChanged()
    }

    private companion object{
        const val ITEM_TYPE_TASK = 0
        const val ITEM_TASK_HEADER = 1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        contextRef = WeakReference(recyclerView.context)
    }

    override fun getItemViewType(position: Int): Int = when(itemList[position]){
        is DashboardItem.TaskItem -> ITEM_TYPE_TASK
        is DashboardItem.HeaderItem -> ITEM_TASK_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ITEM_TYPE_TASK -> createTaskViewHolder(layoutInflater)
            ITEM_TASK_HEADER -> createHeaderViewHolder(layoutInflater)
            else -> throw IllegalStateException("Invalid item type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(val item = itemList[position]) {
            is DashboardItem.TaskItem -> {
                (holder as ViewHolder.TaskItem).display(item.task, onTaskClicked)
                when(position){
                    itemList.lastIndex -> holder.itemView.apply{
                        setPadding(paddingLeft, paddingTop, paddingRight, paddingTop * 2)
                    }
                    else -> holder.itemView.apply {
                        setPadding(paddingLeft, paddingTop, paddingRight, paddingTop)
                    }
                }
            }
            is DashboardItem.HeaderItem -> (holder as ViewHolder.HeaderItem).display(item.headerText)
        }
    }

    private fun createTaskViewHolder(inflater: LayoutInflater): ViewHolder.TaskItem {
        val binding = ListItemTaskBinding.inflate(inflater).apply {
            root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        return ViewHolder.TaskItem(binding)
    }

    private fun createHeaderViewHolder(inflater: LayoutInflater): ViewHolder.HeaderItem {
        val binding = ListItemHeaderBinding.inflate(inflater).apply {
            root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        return ViewHolder.HeaderItem(binding)
    }

    sealed class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        class TaskItem(private val binding: ListItemTaskBinding): ViewHolder(binding.root) {
            fun display(task: Task, onTaskClicked: (Task) -> Unit) {
                binding.taskValue.apply {
                    when (task.status) {
                        TaskStatus.INCOMPLETE -> {
                            paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            setTextColor(context.getColor(R.color.white))
                        }
                        TaskStatus.COMPLETE -> {
                            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            setTextColor(context.getColor(R.color.gray_light))
                        }
                    }
                    text = task.taskName
                }
                binding.root.setOnClickListener { onTaskClicked(task) }
            }
        }
        class HeaderItem(private val binding: ListItemHeaderBinding): ViewHolder(binding.root){
            fun display(title: String) {
                binding.label.text = title
            }
        }
    }

    private fun WeakReference<Context>.require(): Context {
        return get() ?: throw IllegalStateException("No longer attached to context")
    }

    // ===== TASK SWIPE HELPER CALLBACKS =====
    override fun getContext(): Context = contextRef.require()
    override fun canPositionBeSwiped(position: Int): Boolean = !isHeaderAtPosition(position)
    override fun getTaskAtPosition(position: Int): Task = when(val item = itemList[position]) {
        is DashboardItem.TaskItem -> item.task
        else -> throw IllegalStateException("Swiped task at invalid position: $position")
    }

    override fun onTaskSwipe(task: Task, swipe: TaskSwipeHelper.Swipe) = onTaskSwiped(task, swipe)
    override fun getItemCount(): Int = itemList.size

    // ===== STICKY HEADER GETTER =====
    private var currentHeader: Pair<Int, View>? = null

    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        itemPosition.downTo(0).forEach {
            if(isHeaderAtPosition(it)) return it
        }
        return RecyclerView.NO_POSITION
    }

    override fun getCurrentHeader(position: Int): Pair<Int, View>? {
        currentHeader = when(val headerPosition = getHeaderPositionForItem(position)) {
            RecyclerView.NO_POSITION -> null
            currentHeader?.first -> currentHeader
            else -> {
                val viewHolder = createHeaderViewHolder(LayoutInflater.from(contextRef.get()))
                bindViewHolder(viewHolder, headerPosition)
                Pair(headerPosition, viewHolder.itemView)
            }
        }
        return currentHeader
    }

    override fun isHeaderAtPosition(position: Int): Boolean = itemList.getOrNull(position) is DashboardItem.HeaderItem
}