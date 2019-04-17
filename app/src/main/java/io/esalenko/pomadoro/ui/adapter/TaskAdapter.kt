package io.esalenko.pomadoro.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


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

        private val ctx = v.context
        private val taskDuration = v.findViewById<TextView>(R.id.taskDuration)
        private val taskDate = v.findViewById<TextView>(R.id.teskDate)
        private val taskDescription = v.findViewById<TextView>(R.id.taskDescription)

        fun bindView(task: Task) {
            with(task) {
                taskDuration.text =
                    ctx.getString(R.string.task_duration, TimeUnit.MILLISECONDS.toMinutes(task.duration))
                taskDate.text = Date().toString()
                taskDescription.text = description
            }
        }

    }
}