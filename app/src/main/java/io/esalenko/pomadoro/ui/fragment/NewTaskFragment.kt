package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TaskPriority
import io.esalenko.pomadoro.domain.model.TaskType
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewTaskFragment : BaseFragment() {

    companion object {
        const val TAG = "NewTaskFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_new_task

    private val toDoListVIewModel: ToDoListVIewModel by viewModel()

    private val sharedViewModel: SharedViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancelTask.setOnClickListener {
            sharedViewModel.openMainScreen()
        }

        // TODO :: Add RadioGroup listener
        btnSaveTask.setOnClickListener {
            toDoListVIewModel.addTask(
                type = TaskType.WORK.type,
                taskDescription = inputNewTask.text.toString(),
                priority = TaskPriority.MID.ordinal
            )
        }
    }


}