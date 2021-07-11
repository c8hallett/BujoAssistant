package com.hallett.bujoass.presentation.ui.task_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hallett.bujoass.databinding.ListItemTaskBinding
import com.hallett.bujoass.presentation.model.Task

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
        holder.binding.apply{
            val task = itemList[position]
            taskValue.text = task.taskName
            root.setOnClickListener { onTaskClicked(task) }
        }
    }

    override fun getItemCount(): Int = itemList.size

    class ViewHolder(val binding: ListItemTaskBinding): RecyclerView.ViewHolder(binding.root)
}