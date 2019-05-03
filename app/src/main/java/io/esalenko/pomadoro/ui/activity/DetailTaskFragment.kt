package io.esalenko.pomadoro.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.TimerState
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.util.getPriorityColor
import io.esalenko.pomadoro.util.getPriorityIcon
import io.esalenko.pomadoro.vm.TimerViewModel
import kotlinx.android.synthetic.main.fragment_detailed_task.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class DetailTaskFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_detailed_task

    private val timerViewModel: TimerViewModel by sharedViewModel()

    private var taskId: Long = -1L
    private var isCooldown = false
    private var isCompleted = false
    private var currentState: TimerState = TimerState.IDLE

    companion object {
        const val TAG = "DetailTaskFragment"
        private const val KEY_ITEM_ID = "key_item_id"

        @JvmStatic
        fun newInstance(itemId: Long): Fragment {
            return DetailTaskFragment().apply {
                val bundle = Bundle().apply {
                    putLong(KEY_ITEM_ID, itemId)
                }
                arguments = bundle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireNotNull(arguments)

        taskId = arguments.let {
            it?.get(KEY_ITEM_ID) as Long
        }

        if (taskId == -1L) throw IllegalStateException("ItemId can't be -1")

        timerViewModel.getTask(taskId)

        timerHandler.setOnClickListener {
            when (currentState) {
                TimerState.WORKING -> {
                    stopCountdown()
                }
                TimerState.IDLE -> {
                    startCountdown()
                }
            }
        }
        subscribeUi()
        updateTimerView()
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeUi() {
        timerViewModel.apply {
            taskLiveData
                .observe(
                    viewLifecycleOwner,
                    Observer { result: RxResult<Task> ->
                        when (result.status) {
                            RxStatus.LOADING -> {
                                loading.visibility = View.VISIBLE
                            }
                            RxStatus.SUCCESS -> {
                                loading.visibility = View.GONE
                                val task: Task = result.data ?: return@Observer
                                timerViewModel.shareTaskInfo(task)
                                setupTask(task)
                                updateView()
                                updateTimerView()
                            }
                            RxStatus.ERROR -> {
                                loading.visibility = View.GONE

                                Snackbar
                                    .make(parentLayout, " 0", Snackbar.LENGTH_LONG)
                                    .setAction(R.string.snackbar_action_retry) {
                                        timerViewModel.getTask(taskId)
                                    }
                                    .show()
                                updateView()
                                updateTimerView()
                            }
                        }
            })
            getSession(taskId).observe(viewLifecycleOwner, Observer { session: Int? ->
                pomodidorCount.text = "x $session"
            })
            getTaskCooldown(taskId).observe(viewLifecycleOwner, Observer { isCooldown: Boolean? ->
                this@DetailTaskFragment.isCooldown = isCooldown ?: return@Observer
            })

            timerLiveData.observe(viewLifecycleOwner, Observer { timer: String ->
                countdown.text = timer
            })

            timerStateLiveData.observe(viewLifecycleOwner, Observer { state: TimerState ->
                currentState = state
                // TODO :: update view according to state
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTask(task: Task) {
        isCooldown = task.isCooldown
        pomodidorCount.text = "x ${task.pomidors}"
        taskCategory.text = task.category.categoryName

        taskCategory.setCompoundDrawablesWithIntrinsicBounds(
            task.priority.getPriorityIcon(),
            0,
            0,
            0
        )

        taskCategory.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                task.priority.getPriorityColor()
            )
        )

        taskText.text = task.description
        isCompleted = task.isCompleted
    }

    private fun startCountdown() {
        timerViewModel.saveLastStartedTaskId(taskId)
        timerViewModel.setTimerAction(TimerViewModel.TimerAction.START)
        timerViewModel.setTaskInProgress(taskId)
    }

    private fun stopCountdown() {
        timerViewModel.setTimerAction(TimerViewModel.TimerAction.STOP)
    }

    private fun updateTimerView() {
        if (timerViewModel.isLastStartedTask(taskId)) {
            timerContent.visibility = View.VISIBLE
            taskMsg.visibility = View.GONE
        } else {
            timerContent.visibility = View.GONE
            taskMsg.visibility = View.VISIBLE
        }
    }

    private fun updateView() {
        timerHandler.setImageResource(
            when (currentState) {
                TimerState.WORKING -> {
                    R.drawable.ic_round_stop_24px
                }
                TimerState.IDLE -> {
                    R.drawable.ic_round_play_circle_outline_24px
                }
            }
        )

        if (isCompleted) {
            timerContent.visibility = View.GONE
            taskMsg.apply {
                visibility = View.VISIBLE
                text = "Task completed"
            }
        } else {
            timerContent.visibility = View.VISIBLE
            taskMsg.visibility = View.GONE
        }
    }
}