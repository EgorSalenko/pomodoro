package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.task.TaskCategory
import io.esalenko.pomadoro.db.model.task.TaskPriority
import io.esalenko.pomadoro.db.model.task.TaskPriority.*
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.util.avoidDoubleClick
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.ToDoListViewModel
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewTaskFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    companion object {
        const val TAG = "NewTaskFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_new_task

    private val toDoListViewModel: ToDoListViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by sharedViewModel()

    // Default to Low
    private var taskPriority: TaskPriority = LOW
    private var taskCategory: TaskCategory? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerTaskTypes.setOnItemSelectedListener(this)
        radioBtnLow.isChecked = true

        toDoListViewModel.getCategories()

        btnCancelTask.setOnClickListener {
            avoidDoubleClick {
                sharedViewModel.openMainScreen()
            }
        }

        btnSaveTask.setOnClickListener {
            avoidDoubleClick {
                addTask()
            }
        }

        radioGroupPriority.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            taskPriority = when (checkedId) {
                R.id.radioBtnLow -> LOW
                R.id.radioBtnMid -> MID
                R.id.radioBtnHigh -> HIGH
                else -> LOW
            }
        }
        addCategory.setOnClickListener {
            showNewCategoryDialog()
        }
        subscribeUi()
    }

    private fun showNewCategoryDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_new_category)
            input(
                hintRes = R.string.dialog_hint_new_category,
                allowEmpty = false,
                maxLength = 64
            ) { dialogpog, text ->

                val name = text
                    .toString()
                    .trim()
                    .capitalize()

                toDoListViewModel.addCategory(name)
                toDoListViewModel.getCategories()
            }
            positiveButton(android.R.string.ok)
            negativeButton(android.R.string.cancel)
        }
    }

    private fun subscribeUi() {
        toDoListViewModel.apply {
            categoryLiveData.observe(viewLifecycleOwner, Observer { result ->
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        categoryLayout.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                        errorMsg.visibility = View.GONE
                        val list = result.data
                        if (list != null && list.isNotEmpty()) {
                            spinnerTaskTypes.attachDataSource(list)
                            taskCategory = spinnerTaskTypes.selectedItem as TaskCategory
                        }
                    }
                    RxStatus.ERROR -> {
                        errorMsg.visibility = View.VISIBLE
                        categoryLayout.visibility = View.GONE
                        loading.visibility = View.GONE
                    }
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                        errorMsg.visibility = View.GONE
                        categoryLayout.visibility = View.GONE
                    }
                }
            })
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        taskCategory = parent?.adapter?.getItem(position) as TaskCategory
    }

    private fun addTask() {

        val text: String = inputNewTask.text.toString()

        if (validateInput(text)) return

        taskCategory?.let {
            toDoListViewModel.addTask(
                category = it,
                taskDescription = text,
                priority = taskPriority
            )
        }
        sharedViewModel.openMainScreen()
    }

    private fun validateInput(text: String): Boolean {
        if (text.isBlank() or text.isEmpty()) {
            inputNewTaskLayout.error = "Task can't be blank or empty"
            return true
        }

        if (text.length < 2) {
            inputNewTaskLayout.error = "Task is too short"
            return true
        }
        return false
    }
}