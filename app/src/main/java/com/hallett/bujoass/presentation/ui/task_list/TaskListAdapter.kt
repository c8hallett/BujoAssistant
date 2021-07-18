package com.hallett.bujoass.presentation.ui.task_list

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.ListItemTaskBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.model.Task
import timber.log.Timber
import java.lang.ref.WeakReference

class TaskListAdapter(
    val onTaskClicked: (Task) -> Unit,
    val onTaskSwiped: (Task, TaskSwipeHelper.Swipe) -> Unit
): RecyclerView.Adapter<TaskListAdapter.ViewHolder>(), TaskSwipeHelper.SwipeCallbacks {

    private lateinit var contextRef: WeakReference<Context>
    private var itemList: List<Task> = listOf()

    fun setItems(newItems: List<Task>) {
        itemList = newItems
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        contextRef = WeakReference(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.apply {
            layoutParams = MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT).apply {
                val m = context.resources.getDimensionPixelSize(R.dimen.dp12)
                setMargins(0, 0, 0, m)
            }
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = itemList[position]
        holder.binding.taskValue.apply {
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
        holder.binding.root.setOnClickListener { onTaskClicked(task) }
    }

    override fun getItemCount(): Int = itemList.size

    class ViewHolder(val binding: ListItemTaskBinding): RecyclerView.ViewHolder(binding.root)

    // ===== TASK SWIPE HELPER CALLBACKS =====
    override fun getContext(): Context {
        TODO("Not yet implemented")
    }

    override fun getTaskAtPosition(position: Int): Task = itemList[position]
    override fun canPositionBeSwiped(position: Int): Boolean = true
    override fun onTaskSwipe(task: Task, swipe: TaskSwipeHelper.Swipe) = onTaskSwiped(task, swipe)
}