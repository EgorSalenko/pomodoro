package io.esalenko.pomadoro.ui.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TaskPriority
import org.jetbrains.anko.find
import java.util.*


class TaskItem(val text: String, val time: Long, val taskType: String, val taskPriority: Int) :
    AbstractItem<TaskItem, TaskItem.TaskItemViewHolder>() {

    override fun getType(): Int = R.id.fast_item_task

    override fun getViewHolder(v: View): TaskItemViewHolder = TaskItemViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_task

    class TaskItemViewHolder(view: View) : FastAdapter.ViewHolder<TaskItem>(view) {

        private val ctx: Context = view.context

        private val taskType = view.find<TextView>(R.id.taskType)
        private val text = view.find<TextView>(R.id.taskText)
        private val date = view.find<TextView>(R.id.taskDate)
        private val parent = view.find<CardView>(R.id.taskCardView)

        override fun bindView(item: TaskItem, payloads: MutableList<Any>) {
            taskType.text = item.taskType
            text.text = item.text
            date.text = Date(item.time).toString()

            val color = when (item.taskPriority) {
                TaskPriority.LOW.ordinal -> {
                    R.color.priority_low
                }
                TaskPriority.MID.ordinal -> {
                    R.color.priority_mid
                }
                TaskPriority.HIGH.ordinal -> {
                    R.color.priority_high
                }
                else -> android.R.color.white
            }

            parent.setCardBackgroundColor(ContextCompat.getColor(ctx, color))
        }

        override fun unbindView(item: TaskItem) {

        }

    }

}