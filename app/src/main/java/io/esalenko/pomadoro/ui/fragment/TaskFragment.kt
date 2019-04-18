package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.BaseFragment
import io.esalenko.pomadoro.ui.adapter.TaskAdapter
import io.esalenko.pomadoro.vm.SharedCountdownViewModel
import io.esalenko.pomadoro.vm.TaskViewModel
import kotlinx.android.synthetic.main.fragment_task.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TaskFragment : BaseFragment() {

    companion object {
        const val TAG = "TaskFragment"
    }

    private lateinit var sharedCountdownViewModel: SharedCountdownViewModel
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override val layoutRes: Int
        get() = R.layout.fragment_task

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedCountdownViewModel = getSharedViewModel()

        taskViewModel = getViewModel()

        taskAdapter = TaskAdapter()
        tasksRecycler.adapter = taskAdapter
        tasksRecycler.layoutManager = LinearLayoutManager(context)
        subscribeUi()
        observeTaskEditText()
    }

    private fun subscribeUi() {
        sharedCountdownViewModel.apply {
            sessionsLiveData.observe(viewLifecycleOwner, Observer { sessions: Int ->
                sessionsCount.text = getString(R.string.session, sessions)
            })
        }

        taskViewModel.apply {
            taskLiveData.observe(viewLifecycleOwner, Observer { tasks: List<Task> ->
                taskAdapter.addTasks(tasks)
            })
        }
    }

    private fun observeTaskEditText() {
        inputTaskDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                taskViewModel.cacheTask(sequence.toString())
            }

        })
    }
}