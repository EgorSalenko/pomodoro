package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.BaseFragment
import io.esalenko.pomadoro.vm.SharedCountdownViewModel
import kotlinx.android.synthetic.main.fragment_work_timer.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class WorkTimerFragment : BaseFragment() {

    companion object {
        const val TAG = "WorkTimerFragment"
    }

    private lateinit var viewModel: SharedCountdownViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_work_timer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getSharedViewModel()
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.apply {

            timerLiveData.observe(viewLifecycleOwner, Observer { time: String ->
                countdownTimer.text = time
            })

            statusLiveData.observe(viewLifecycleOwner, Observer { status: String ->
                timerStatus.text = status
            })
        }
    }

}