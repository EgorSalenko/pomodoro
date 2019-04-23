package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TaskCategory
import io.esalenko.pomadoro.domain.model.TaskPriority
import io.esalenko.pomadoro.domain.model.TaskPriority.*
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewTaskFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    companion object {
        const val TAG = "NewTaskFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_new_task

    private val toDoListVIewModel: ToDoListVIewModel by viewModel()
    private val sharedViewModel: SharedViewModel by sharedViewModel()

    // Default to Low
    private var taskPriority: TaskPriority = LOW
    private var taskCategory: TaskCategory = TaskCategory.NONE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true
        spinnerTaskTypes.onItemSelectedListener = this
        radioBtnLow.isChecked = true

        ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item,
            TaskCategory.values().filter {
                it != TaskCategory.NONE
            }
        ).also { adapter: ArrayAdapter<TaskCategory> ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTaskTypes.adapter = adapter
        }

        btnCancelTask.setOnClickListener {
            sharedViewModel.openMainScreen()
        }

        btnSaveTask.setOnClickListener {
            addTask()
        }

        radioGroupPriority.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
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
            category = taskCategory.ordinal,
            taskDescription = text,
            priority = taskPriority.ordinal
        )
        sharedViewModel.openMainScreen()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        taskCategory = parent?.getItemAtPosition(position) as TaskCategory
    }
}