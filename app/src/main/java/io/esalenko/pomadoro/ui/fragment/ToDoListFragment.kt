package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import org.jetbrains.anko.info
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToDoListFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_to_do_list

    private val toDoListVIewModel: ToDoListVIewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toDoListVIewModel.apply {
            toDoListLiveData.observe(viewLifecycleOwner, Observer { toDoList: List<Task> ->
                toDoList.forEach {
                    info { it }
                }
            })
        }
    }

}