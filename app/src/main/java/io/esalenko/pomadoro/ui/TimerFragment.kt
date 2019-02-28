package io.esalenko.pomadoro.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.vm.CountdownViewModel
import io.esalenko.pomadoro.vm.SharedViewModel
import kotlinx.android.synthetic.main.fragment_timer.*
import java.util.concurrent.TimeUnit


class TimerFragment : BaseFragment() {

    companion object {
        const val TAG = "TimerFragment"
    }

    private lateinit var viewModel: CountdownViewModel

    private lateinit var sharedViewModel: SharedViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_timer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel(CountdownViewModel::class)

        sharedViewModel = activity.run {
            this?.getSharedViewModel(SharedViewModel::class)
        }
            ?: throw IllegalArgumentException("$sharedViewModel not attached to ${this@TimerFragment.javaClass.simpleName}")

        subscribeUi(viewModel)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        tv_fragment_timer_timer.setOnClickListener { view: View ->
            startCountdown()
        }
    }

    private fun subscribeUi(viewModel: CountdownViewModel) {
        viewModel.updateCountdownLiveData.observe(viewLifecycleOwner, Observer { time: Long ->

            val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60

            val result = String.format("%02d:%02d", minutes, seconds)

            tv_fragment_timer_timer.text = result
        })

        viewModel.taskDescriptionLiveData.observe(viewLifecycleOwner, Observer { taskDescription: String? ->
            if (taskDescription != null) {
                tv_fragment_timer_task.text = taskDescription
            }
        })

        sharedViewModel.isActivatedLiveData.observe(viewLifecycleOwner, Observer { isStarted: Boolean ->

            tv_fragment_timer_timer.visibility = if (isStarted) View.VISIBLE else View.GONE
            til_fragment_timer_task_description.visibility = if (isStarted) View.GONE else View.VISIBLE
            tv_fragment_timer_task.visibility = if (isStarted) View.VISIBLE else View.GONE

            if (isStarted) {
                startCountdown()
            } else {
                stopCountdown()
            }

        })
    }

    private fun startCountdown() {
        viewModel.setTaskDescription(et_fragment_timer.text.toString())
        viewModel.startTimer()
    }

    private fun stopCountdown() {
        viewModel.stopTimer()
    }

}