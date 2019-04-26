package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.util.getPriorityColor
import io.esalenko.pomadoro.util.getPriorityIcon
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.SharedViewModel.TimerState
import io.esalenko.pomadoro.vm.TimerViewModel
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.fragment_task.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskFragment : BaseFragment() {

    private var itemId: Long = -1L

    override val layoutRes: Int
        get() = R.layout.fragment_task

    private val timerViewModel: TimerViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by sharedViewModel()
    private var currentState: TimerState = TimerState.FINISHED

    companion object {
        const val TAG = "TaskFragment"
        private const val KEY_ITEM_ID = "key_item_id"

        fun newInstance(itemId: Long): Fragment {
            return TaskFragment().apply {
                val bundle = Bundle()
                bundle.putLong(KEY_ITEM_ID, itemId)
                arguments = bundle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemId = arguments!![KEY_ITEM_ID] as Long
        timerViewModel.getTask(itemId)
        timerHandler.setOnClickListener {
            when (currentState) {
                TimerState.WORKING -> {
                    sharedViewModel.stopTimer()
                }
                TimerState.FINISHED, TimerState.STOPPED -> {
                    timerViewModel.saveLastStartedTaskId(itemId)
                    sharedViewModel.startTimer()
                }
            }
        }
        subscribeUi()
        updateView()
    }

    private fun updateView() {
        timerHandler.setImageResource(
            when (currentState) {
                TimerState.WORKING -> {
                    R.drawable.ic_round_stop_24px
                }
                TimerState.FINISHED -> {
                    R.drawable.ic_round_play_circle_outline_24px
                }
                TimerState.STOPPED -> {
                    R.drawable.ic_round_play_circle_outline_24px
                }
            }
        )
    }

    private fun subscribeUi() {
        timerViewModel.apply {
            taskLiveData.observe(viewLifecycleOwner, Observer { result: RxResult<Task> ->
                when (result.status) {
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                    }
                    RxStatus.SUCCESS -> {
                        loading.visibility = View.GONE
                        val task = result.data ?: return@Observer

                        pomodidorCount.text = task.pomidors.toString()
                        taskCategory.text = task.category.categoryName
                        taskCategory.setCompoundDrawablesWithIntrinsicBounds(
                            task.priority.getPriorityIcon(),
                            0,
                            0,
                            0
                        )
                        taskCategory.setTextColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                task.priority.getPriorityColor()
                            )
                        )
                        taskText.text = task.description

                    }
                    RxStatus.ERROR -> {
                        loading.visibility = View.GONE
                        Snackbar.make(parentTimer, " 0", Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_action_retry) {
                                timerViewModel.getTask(itemId)
                            }
                            .show()
                    }
                }
            })
        }
        sharedViewModel.apply {
            timerLiveData.observe(viewLifecycleOwner, Observer { formattedTime: String ->
                if (timerViewModel.isLastStartedTask(itemId)) {
                    countdown.text = formattedTime
                } else {
                    timerContent.visibility = View.GONE
                }
            })
            timerStateLiveData.observe(viewLifecycleOwner, Observer { state: TimerState ->
                currentState = state
                updateView()
            })
        }
    }
}

