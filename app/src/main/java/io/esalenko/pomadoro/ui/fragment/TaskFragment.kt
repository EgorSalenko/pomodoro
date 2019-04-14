package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.BaseFragment
import io.esalenko.pomadoro.vm.SharedCountdownViewModel
import kotlinx.android.synthetic.main.fragment_task.*


class TaskFragment : BaseFragment() {

    private lateinit var viewModel: SharedCountdownViewModel

    companion object {
        const val TAG = "TaskFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_task

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            this.getSharedViewModel(SharedCountdownViewModel::class)
        } ?: throw NoSuchElementException("Activity for ${TaskFragment::class.simpleName} not found")

        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.apply {
            sessionsLiveData.observe(viewLifecycleOwner, Observer { sessions: Int ->
                sessionsCount.text = String.format("Session : %s", sessions)
            })
        }
    }

}