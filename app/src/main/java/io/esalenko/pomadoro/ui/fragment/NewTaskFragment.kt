package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.task.Category
import io.esalenko.pomadoro.db.model.task.Priority
import io.esalenko.pomadoro.db.model.task.Priority.*
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
    private var priority: Priority = LOW
    private var category: Category? = null

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
            priority = when (checkedId) {
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
                            category = spinnerTaskTypes.selectedItem as Category
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
            categoryRxLiveData.observe(viewLifecycleOwner, Observer { categories: List<Category> ->
                if (categories == null && categories.isEmpty()) return@Observer

                spinnerTaskTypes.attachDataSource(categories)
                category = spinnerTaskTypes.selectedItem as Category
            })
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        category = parent?.adapter?.getItem(position) as Category
    }

    private fun addTask() {

        val text: String = inputNewTask.text.toString()

        if (validateInput(text)) return

        category?.let {
            toDoListViewModel.addTask(
                category = it,
                taskDescription = text,
                priority = priority
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