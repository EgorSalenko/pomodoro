package io.esalenko.pomadoro.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task


class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val tasks = ArrayList<Task>()

    fun addTasks(list: List<Task>) {
        tasks.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder = TaskViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_task, parent, false
        )
    )


    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindView(tasks[position])
    }

    class TaskViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bindView(task: Task) {
            with(task) {

            }
        }

    }
}