package io.esalenko.pomadoro.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.task.TaskPriority
import io.esalenko.pomadoro.util.formatDate
import io.esalenko.pomadoro.util.getPriorityColor
import io.esalenko.pomadoro.util.getPriorityIcon
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import java.util.*


class TaskItem(
    val id: Long,
    val text: String,
    val date: Date?,
    val taskType: String,
    val taskPriority: TaskPriority,
    val pomidors: Int,
    val isInProgress: Boolean,
    val isCompleted: Boolean
) :
    AbstractItem<TaskItem, TaskItem.TaskItemViewHolder>(),
    ISwipeable<TaskItem, IItem<*, *>> {

    var swipedDirection: Int = 0
    var swipedAction: Runnable? = null
    var swipeable: Boolean = true

    override fun getIdentifier(): Long = id

    override fun withIsSwipeable(swipeable: Boolean): TaskItem {
        this.swipeable = swipeable
        return this
    }

    override fun isSwipeable(): Boolean = swipeable

    override fun getType(): Int = R.id.fast_item_task

    override fun getViewHolder(v: View): TaskItemViewHolder = TaskItemViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_task

    override fun bindView(viewHolder: TaskItemViewHolder, payloads: MutableList<Any>) {
        super.bindView(viewHolder, payloads)
        viewHolder.also {
            it.taskType.text = taskType
            it.text.text = text
            it.date.text = date?.formatDate()
            it.pomidorsCounter.text = "x $pomidors"
            it.taskStatus.visibility = if (isInProgress) View.VISIBLE else View.GONE
            it.completeStatus.visibility = if (isCompleted) View.VISIBLE else View.GONE
        }
        val priorityColor = taskPriority.getPriorityColor()
        val priorityDrawableRes = taskPriority.getPriorityIcon()

        val priorityDrawable = ContextCompat.getDrawable(viewHolder.ctx, priorityDrawableRes)

        viewHolder.taskType.apply {
            setCompoundDrawablesWithIntrinsicBounds(priorityDrawable, null, null, null)
            setTextColor(ContextCompat.getColor(viewHolder.ctx, priorityColor))
        }

        viewHolder.apply {
            swipeResultContent.visibility = if (swipedDirection != 0) View.VISIBLE else View.INVISIBLE
            itemContent.visibility = if (swipedDirection != 0) View.INVISIBLE else View.VISIBLE
        }

        var swipedAction: CharSequence? = null
        var swipedText: CharSequence? = null
        if (swipedDirection != 0) {
            swipedAction = viewHolder.itemView.context.getString(R.string.action_undo)
            swipedText = if (swipedDirection == ItemTouchHelper.LEFT) "Removed" else "Archived"
            viewHolder.completeStatus.visibility = View.GONE
            viewHolder.swipeResultContent.setBackgroundColor(
                ContextCompat.getColor(
                    viewHolder.itemView.context,
                    if (swipedDirection == ItemTouchHelper.LEFT) R.color.md_red_900 else R.color.md_blue_900
                )
            )
        }
        viewHolder.also {
            it.swipedAction.text = swipedAction ?: ""
            it.swipedText.text = swipedText ?: ""
            it.swipedActionRunnable = this.swipedAction
        }

    }

    override fun toString(): String {
        return "id: Long = $id, " +
                "text: String =$text, " +
                "date: Date? = ${date.toString()}, " +
                "taskType: String =$taskType, " +
                "taskPriority: TaskPriority = ${taskPriority.name}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as TaskItem

        if (id != other.id) return false
        if (text != other.text) return false
        if (date != other.date) return false
        if (taskType != other.taskType) return false
        if (taskPriority != other.taskPriority) return false
        if (pomidors != other.pomidors) return false
        if (isInProgress != other.isInProgress) return false
        if (isCompleted != other.isCompleted) return false
        if (swipedDirection != other.swipedDirection) return false
        if (swipedAction != other.swipedAction) return false
        if (swipeable != other.swipeable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + taskType.hashCode()
        result = 31 * result + taskPriority.hashCode()
        result = 31 * result + pomidors
        result = 31 * result + isInProgress.hashCode()
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + swipedDirection
        result = 31 * result + (swipedAction?.hashCode() ?: 0)
        result = 31 * result + swipeable.hashCode()
        return result
    }


    @SuppressLint("CheckResult", "ClickableViewAccessibility")
    inner class TaskItemViewHolder(view: View) : RecyclerView.ViewHolder(view), AnkoLogger {

        val ctx: Context = view.context
        val taskType = view.find<TextView>(R.id.taskType)
        val text = view.find<TextView>(R.id.taskText)
        val date = view.find<TextView>(R.id.taskDate)
        val pomidorsCounter = view.find<TextView>(R.id.pomidorsCounter)
        val taskStatus = view.find<TextView>(R.id.taskStatus)

        val swipedAction = view.find<TextView>(R.id.swiped_action)
        val swipedText = view.find<TextView>(R.id.swiped_text)
        val swipeResultContent = view.find<LinearLayout>(R.id.swipe_result_content)
        val itemContent = view.find<ConstraintLayout>(R.id.item_content)
        val completeStatus = view.find<TextView>(R.id.completedStatus)

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