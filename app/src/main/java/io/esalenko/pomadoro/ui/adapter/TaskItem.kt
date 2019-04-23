package io.esalenko.pomadoro.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter_extensions.drag.IDraggable
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TaskPriority
import io.esalenko.pomadoro.util.formatDate
import org.jetbrains.anko.find
import java.util.*


class TaskItem(
    val id: Long,
    val text: String,
    val date: Date?,
    val taskType: String,
    val taskPriority: TaskPriority
) :
    AbstractItem<TaskItem, TaskItem.TaskItemViewHolder>(),
    ISwipeable<TaskItem, IItem<*, *>>,
    IDraggable<TaskItem, IItem<*, *>> {

    var swipedDirection: Int = 0
    var swipedAction: Runnable? = null
    var swipeable: Boolean = true
    var draggable: Boolean = true

    override fun withIsSwipeable(swipeable: Boolean): TaskItem {
        this.swipeable = swipeable
        return this
    }

    override fun isSwipeable(): Boolean = swipeable

    override fun withIsDraggable(draggable: Boolean): TaskItem {
        this.draggable = draggable
        return this
    }

    override fun isDraggable(): Boolean = draggable

    override fun getType(): Int = R.id.fast_item_task

    override fun getViewHolder(v: View): TaskItemViewHolder = TaskItemViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_task

    @SuppressLint("CheckResult")
    override fun bindView(viewHolder: TaskItemViewHolder, payloads: MutableList<Any>) {
        super.bindView(viewHolder, payloads)
        viewHolder.taskType.text = taskType
        viewHolder.text.text = text

        viewHolder.date.text = date?.formatDate()

        val priorityColor = when (taskPriority) {
            TaskPriority.LOW -> {
                R.color.priority_low
            }
            TaskPriority.MID -> {
                R.color.priority_mid
            }
            TaskPriority.HIGH -> {
                R.color.priority_high
            }
        }

        viewHolder.apply {
            parent.setCardBackgroundColor(ContextCompat.getColor(viewHolder.ctx, priorityColor))
            swipeResultContent.visibility = if (swipedDirection != 0) View.VISIBLE else View.INVISIBLE
            itemContent.visibility = if (swipedDirection != 0) View.INVISIBLE else View.VISIBLE
        }

        var swipedAction: CharSequence? = null
        var swipedText: CharSequence? = null
        if (swipedDirection != 0) {
            swipedAction = viewHolder.itemView.context.getString(R.string.action_undo)
            swipedText = if (swipedDirection == ItemTouchHelper.LEFT) "Removed" else "Archived"
            viewHolder.swipeResultContent.setBackgroundColor(
                ContextCompat.getColor(
                    viewHolder.itemView.context,
                    if (swipedDirection == ItemTouchHelper.LEFT) R.color.md_red_900 else R.color.primaryColor
                )
            )
        }
        viewHolder.swipedAction.text = swipedAction ?: ""
        viewHolder.swipedText.text = swipedText ?: ""
        viewHolder.swipedActionRunnable = this.swipedAction

    }

    inner class TaskItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ctx: Context = view.context
        val taskType = view.find<TextView>(R.id.taskType)
        val text = view.find<TextView>(R.id.taskText)
        val date = view.find<TextView>(R.id.taskDate)
        val parent = view.find<CardView>(R.id.taskCardView)

        val swipedAction = view.find<TextView>(R.id.swiped_action)
        val swipedText = view.find<TextView>(R.id.swiped_text)
        val swipeResultContent = view.find<LinearLayout>(R.id.swipe_result_content)
        val itemContent = view.find<LinearLayout>(R.id.item_content)

        var swipedActionRunnable: Runnable? = null

        init {
            swipedAction.setOnClickListener {
                if (swipedActionRunnable != null) {
                    swipedActionRunnable?.run()
                }
            }
        }
    }

}