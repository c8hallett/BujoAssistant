package com.hallett.bujoass.presentation.ui.task_list

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hallett.bujoass.R
import com.hallett.bujoass.databinding.ListItemTaskBinding
import com.hallett.bujoass.domain.model.TaskStatus
import com.hallett.bujoass.presentation.model.Task
import timber.log.Timber

class TaskListAdapter(val onTaskClicked: (Task) -> Unit): RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    private var itemList: List<Task> = listOf()

    fun setItems(newItems: List<Task>) {
        itemList = newItems
        notifyDataSetChanged()
    }

    fun getTaskAtPosition(position: Int): Task? = itemList.getOrNull(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.context))
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
}