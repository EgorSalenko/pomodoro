package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.adapter.TaskItem
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToDoListFragment : BaseFragment() {

    companion object {
        const val TAG = "ToDoListFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_to_do_list

    private val toDoListVIewModel: ToDoListVIewModel by viewModel()

    private lateinit var fastAdapter: FastAdapter<TaskItem>
    private lateinit var itemAdapter: ItemAdapter<TaskItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        toDoListVIewModel.fetchToDoList()
        subscribeUi()
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter.items()
        fastAdapter = FastAdapter.with(itemAdapter)
        toDoList.layoutManager = LinearLayoutManager(context)
        toDoList.adapter = fastAdapter
    }

    private fun subscribeUi() {
        toDoListVIewModel.apply {
            toDoListLiveData.observe(viewLifecycleOwner, Observer { result: RxResult<List<Task>> ->
                val items = ArrayList<TaskItem>()
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        loading.visibility = View.GONE
                        result
                            .data
                            ?.forEach { task: Task ->
                                items.add(
                                    TaskItem(
                                        task.description,
                                        task.date,
                                        task.type,
                                        task.priority
                                    )
                                )
                            }
                        FastAdapterDiffUtil.set(itemAdapter, items)
                    }
                    RxStatus.ERROR -> {
                        Snackbar.make(toDoList, "Error occurred while tasks loading", Snackbar.LENGTH_SHORT)
                    }
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                    }
                }

            })
        }
    }

}