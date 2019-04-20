package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TaskPriority
import io.esalenko.pomadoro.domain.model.TaskPriority.*
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

    // Default to Low
    private var taskPriority: TaskPriority = LOW

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioBtnLow.isChecked = true

        btnCancelTask.setOnClickListener {
            sharedViewModel.openMainScreen()
        }

        // TODO :: Add RadioGroup listener
        btnSaveTask.setOnClickListener {
            addTask()
        }

        radioGroupPriority.setOnCheckedChangeListener { group: RadioGroup, checkedId: Int ->
            taskPriority = when (checkedId) {
                R.id.radioBtnLow -> LOW
                R.id.radioBtnMid -> MID
                R.id.radioBtnHigh -> HIGH
                else -> LOW
            }
        }
    }

    private fun addTask() {

        val text = inputNewTask.text.toString()

        if (text.isBlank() or text.isEmpty()) {
            inputNewTaskLayout.error = "Task can't be blank or empty"
            return
        }

        if (text.length < 2) {
            inputNewTaskLayout.error = "Task is too short"
            return
        }

        toDoListVIewModel.addTask(
            type = TaskType.WORK.type,
            taskDescription = text,
            priority = taskPriority.ordinal
        )
        sharedViewModel.openMainScreen()
    }


}