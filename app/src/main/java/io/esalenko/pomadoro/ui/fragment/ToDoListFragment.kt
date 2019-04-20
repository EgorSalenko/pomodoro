package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import org.jetbrains.anko.info
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToDoListFragment : BaseFragment() {

    companion object {
        const val TAG = "ToDoListFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_to_do_list

    private val toDoListVIewModel: ToDoListVIewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUi()
    }

    private fun subscribeUi() {
        toDoListVIewModel.apply {
            toDoListLiveData.observe(viewLifecycleOwner, Observer { result: RxResult<List<Task>> ->
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        result.data?.forEach { task: Task ->
                            info { task }
                        }
                    }
                    RxStatus.ERROR -> {

                    }
                    RxStatus.LOADING -> {

                    }
                }

            })
        }
    }

}