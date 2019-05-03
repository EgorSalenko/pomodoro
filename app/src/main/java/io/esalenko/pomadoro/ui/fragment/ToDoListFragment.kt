package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.FilterType
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.ui.adapter.TaskItem
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.util.avoidDoubleClick
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import org.jetbrains.anko.info
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToDoListFragment : BaseFragment(), SimpleSwipeCallback.ItemSwipeCallback {

    companion object {
        const val TAG = "ToDoListFragment"
    }

    private var mLastClickTime: Byte = 0
    override val layoutRes: Int
        get() = R.layout.fragment_to_do_list

    private val viewModel: ToDoListVIewModel by viewModel()
    private val sharedViewModel: SharedViewModel by sharedViewModel()

    private lateinit var fastAdapter: FastAdapter<TaskItem>
    private lateinit var itemAdapter: ItemAdapter<TaskItem>

    private lateinit var touchCallback: SimpleSwipeCallback
    private lateinit var touchHelper: ItemTouchHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.fetchToDoList()
        subscribeUi()
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter.items()
        fastAdapter = FastAdapter.with(itemAdapter)
        toDoList.layoutManager = LinearLayoutManager(context)
        toDoList.adapter = fastAdapter
        toDoList.itemAnimator = SlideDownAlphaAnimator()

        val leaveBehindDrawableLeft = context?.getDrawable(R.drawable.ic_round_delete_sweep_24px)

        val leaveBehindDrawableRight = context?.getDrawable(R.drawable.ic_round_archive_24px)

        touchCallback = SimpleSwipeCallback(
            this,
            leaveBehindDrawableLeft,
            ItemTouchHelper.LEFT,
            ContextCompat.getColor(context!!, R.color.md_red_900)
        )
            .withBackgroundSwipeRight(ContextCompat.getColor(context!!, R.color.md_blue_900))
            .withLeaveBehindSwipeRight(leaveBehindDrawableRight)

        touchHelper =
            ItemTouchHelper(touchCallback).also {
                it.attachToRecyclerView(toDoList)
            }

        fastAdapter.withOnClickListener { _, _, item: TaskItem, _ ->
            avoidDoubleClick {
                sharedViewModel.openDetailTaskScreen(item.id, item.isCompleted)
            }
            true
        }
    }

    private fun subscribeUi() {

        viewModel.apply {

            toDoListLiveData.observe(viewLifecycleOwner, Observer { result: RxResult<List<Task>> ->
                val items = ArrayList<TaskItem>()
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        itemAdapter.clear()
                        if (result.data?.isEmpty()!!) {
                            warning_msg_empty_list.visibility = View.VISIBLE
                        } else {
                            warning_msg_empty_list.visibility = View.GONE
                            result
                                .data
                                .forEach { task: Task ->
                                    items.add(
                                        TaskItem(
                                            task.id,
                                            task.description,
                                            task.date,
                                            task.category.categoryName,
                                            task.priority,
                                            task.pomidors,
                                            task.isRunning,
                                            task.isCompleted
                                        )
                                    )
                                    info { task }
                                }
                            FastAdapterDiffUtil.set(itemAdapter, items)
                        }
                        loading.visibility = View.GONE
                    }
                    RxStatus.ERROR -> {
                        loading.visibility = View.GONE
                        warning_msg_empty_list.visibility = View.VISIBLE
                        sharedViewModel.showError(result.msg)
                    }
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                    }
                }

            })
            observeToDoList().observe(viewLifecycleOwner, Observer { taskList: List<Task> ->
                val items = ArrayList<TaskItem>()
                taskList
                    .forEach { task: Task ->
                        items.add(
                            TaskItem(
                                task.id,
                                task.description,
                                task.date,
                                task.category.categoryName,
                                task.priority,
                                task.pomidors,
                                task.isRunning,
                                task.isCompleted
                            )
                        )
                        info { task }
                    }
                FastAdapterDiffUtil.set(itemAdapter, items)
            })
        }

        sharedViewModel.apply {

            filterLiveData.observe(viewLifecycleOwner, Observer { event: Event<FilterType> ->
                when (event.getContentIfNotHandled()) {
                    FilterType.BY_PRIORITY -> {
                        viewModel.getToDoListByPriority()
                    }
                    FilterType.BY_DATE -> {
                        viewModel.fetchToDoList()
                    }
                    FilterType.BY_ARCHIVED -> {
                        viewModel.getToDoListArchived()
                    }
                    FilterType.BY_COMPLETED -> {
                        viewModel.getToDoListCompleted()
                    }
                }
            })

            errorRetryLiveData.observe(viewLifecycleOwner, Observer {
                viewModel.fetchToDoList()
            })
        }
    }

    override fun itemSwiped(position: Int, direction: Int) {
        val item = itemAdapter.getAdapterItem(position)
        item.swipedDirection = direction
        info { item.swipedDirection }

        val removeRunnable = Runnable {
            item.swipedAction = null
            val _position: Int = itemAdapter.getAdapterPosition(item)
            if (_position != RecyclerView.NO_POSITION) {
                itemAdapter.remove(_position)

                ItemTouchHelper.ACTION_STATE_DRAG
                when (item.swipedDirection) {
                    ItemTouchHelper.LEFT -> viewModel.remove(item.id)
                    ItemTouchHelper.RIGHT -> viewModel.archive(item.id)
                }
            }
        }

        toDoList.postDelayed(removeRunnable, 3000)

        item.swipedAction = Runnable {
            toDoList.removeCallbacks(removeRunnable)
            item.swipedDirection = 0
            val _position = itemAdapter.getAdapterPosition(item)
            if (_position != RecyclerView.NO_POSITION) {
                fastAdapter.notifyItemChanged(_position)
            }
        }

        fastAdapter.notifyItemChanged(position)
    }
}