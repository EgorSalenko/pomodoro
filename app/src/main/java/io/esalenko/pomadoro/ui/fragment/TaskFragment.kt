package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.BaseFragment
import io.esalenko.pomadoro.ui.adapter.TaskAdapter
import io.esalenko.pomadoro.vm.TaskViewModel
import kotlinx.android.synthetic.main.fragment_task.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TaskFragment : BaseFragment() {

    companion object {
        const val TAG = "TaskFragment"
    }

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override val layoutRes: Int
        get() = R.layout.fragment_task

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = getViewModel()

        taskAdapter = TaskAdapter()
        tasksRecycler.adapter = taskAdapter
        tasksRecycler.layoutManager = LinearLayoutManager(context)
        subscribeUi()
    }

    private fun subscribeUi() {
        taskViewModel.apply {
            taskLiveData.observe(viewLifecycleOwner, Observer { tasks: List<Task> ->
                taskAdapter.addTasks(tasks)
            })
        }
    }


}