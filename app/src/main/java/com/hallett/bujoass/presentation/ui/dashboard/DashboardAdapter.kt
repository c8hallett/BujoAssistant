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
import org.kodein.di.weakReference
import timber.log.Timber
import java.lang.ref.WeakReference

class DashboardAdapter(
    private val context: WeakReference<Context>,
    private val onTaskClicked: (Task) -> Unit): RecyclerView.Adapter<DashboardAdapter.ViewHolder>(), StickyHeaderDecoration.StickyHeaderGetter {

    private var itemList: List<DashboardItem> = listOf()

    fun setItems(newItems: List<DashboardItem>) {
        itemList = newItems
        notifyDataSetChanged()
    }

    private companion object{
        const val ITEM_TYPE_TASK = 0
        const val ITEM_TASK_HEADER = 1
    }


    override fun getItemCount(): Int = itemList.size

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
            is DashboardItem.TaskItem -> (holder as ViewHolder.TaskItem).display(item.task, onTaskClicked)
            is DashboardItem.HeaderItem -> (holder as ViewHolder.HeaderItem).display(item.headerText)
        }
    }

    private fun createTaskViewHolder(inflater: LayoutInflater): ViewHolder.TaskItem {
        val binding = ListItemTaskBinding.inflate(inflater)
        binding.root.apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            ).apply {
                val m = context.resources.getDimensionPixelSize(R.dimen.dp12)
                setMargins(m, m, m, 0)
            }
        }
        return ViewHolder.TaskItem(binding)
    }

    private fun createHeaderViewHolder(inflater: LayoutInflater): ViewHolder.HeaderItem {
        val binding = ListItemHeaderBinding.inflate(inflater)
        binding.root.apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            ).apply {
                val m = context.resources.getDimensionPixelSize(R.dimen.dp12)
                setMargins(m, m, m, 0)
            }
        }
        return ViewHolder.HeaderItem(binding)
    }

    sealed class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        class TaskItem(private val binding: ListItemTaskBinding): ViewHolder(binding.root) {
            fun display(task: Task, onTaskClicked: (Task) -> Unit) {
                binding.taskValue.apply {
                    Timber.i("displaying task $task with status ${task.status}")
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

    // ===== STICKY HEADER GETTER =====
    private var currentHeader: Pair<Int, View>? = null

    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        (itemPosition..0).forEach {
            if(isHeader(it)) return it
        }
        return RecyclerView.NO_POSITION
    }

    override fun getCurrentHeader(position: Int): Pair<Int, View>? {
        return when(val headerPosition = getHeaderPositionForItem(position)) {
            RecyclerView.NO_POSITION -> null
            currentHeader?.first -> currentHeader
            else -> {
                context.get()?.let {
                    val viewHolder = createHeaderViewHolder(LayoutInflater.from(context.get()))
                    bindViewHolder(viewHolder, headerPosition)
                    Pair(headerPosition, viewHolder.itemView).also { currentHeader = it }
                } ?: throw IllegalStateException("No longer attached to context")
            }
        }
    }

    override fun isHeader(position: Int): Boolean = itemList.getOrNull(position) is DashboardItem.HeaderItem
}