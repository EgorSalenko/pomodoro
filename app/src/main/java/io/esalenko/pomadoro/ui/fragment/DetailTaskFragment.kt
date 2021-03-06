package io.esalenko.pomadoro.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.TimerState
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.*
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

        startTimer.setOnClickListener {
            avoidDoubleClick {
                startCountdown()
            }
        }

        stopTimer.setOnClickListener {
            avoidDoubleClick {
                stopCountdown()
            }
        }
        subscribeUi()
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
                            }
                        }
                    })
            getRxTaskLiveData(taskId).observe(viewLifecycleOwner, Observer { task ->
                if (task == null) return@Observer
                timerViewModel.shareTaskInfo(task)
                setupTask(task)
                updateView()
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
                updateView()
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTask(task: Task) {
        TransitionManager.beginDelayedTransition(timerCardView)
        isCooldown = task.isCooldown
        textSessionName.text =
            if (task.isCooldown) getString(R.string.text_cooldown_session) else getString(R.string.text_work_session)
        pomodidorCount.text = "x ${task.pomidors}"
        taskCategory.text = task.category?.categoryName

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
        TransitionManager.endTransitions(timerCardView)
    }

    private fun startCountdown() {
        timerViewModel.saveLastStartedTaskId(taskId)
        timerViewModel.setTimerAction(TimerViewModel.TimerAction.START)
        timerViewModel.setTaskInProgress(taskId)
    }

    private fun stopCountdown() {
        timerViewModel.setTimerAction(TimerViewModel.TimerAction.STOP)
    }

    private fun updateView() {
        if (timerViewModel.isLastStartedTask(taskId)) {
            when (currentState) {
                TimerState.WORKING -> {
                    TransitionManager.beginDelayedTransition(timerCardView)
                    startTimer.visibility = View.GONE
                    stopTimer.visibility = View.VISIBLE
                    timerContent.visibility = View.VISIBLE
                    taskMsg.visibility = View.GONE
                    TransitionManager.endTransitions(timerCardView)
                }
                TimerState.IDLE -> {
                    if (isCompleted) {
                        TransitionManager.beginDelayedTransition(timerCardView)
                        startTimer.visibility = View.GONE
                        stopTimer.visibility = View.GONE
                        timerContent.visibility = View.GONE
                        taskMsg.apply {
                            text = "Task completed"
                            visibility = View.VISIBLE
                        }
                        TransitionManager.endTransitions(timerCardView)
                    } else {
                        countdown.text = "00:00"
                        TransitionManager.beginDelayedTransition(timerCardView)
                        timerContent.visibility = View.VISIBLE
                        startTimer.visibility = View.VISIBLE
                        stopTimer.visibility = View.GONE
                        taskMsg.apply {
                            visibility = View.GONE
                        }
                        TransitionManager.endTransitions(timerCardView)
                    }
                }
            }
        }
    }
}
