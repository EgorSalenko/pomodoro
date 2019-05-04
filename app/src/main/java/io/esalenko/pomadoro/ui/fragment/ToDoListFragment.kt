package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
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
import io.esalenko.pomadoro.db.model.Filter
import io.esalenko.pomadoro.db.model.task.Priority
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.ui.adapter.TaskItem
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.util.avoidDoubleClick
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.ToDoListViewModel
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import org.jetbrains.anko.info
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToDoListFragment : BaseFragment(), SimpleSwipeCallback.ItemSwipeCallback {

    companion object {
        const val TAG = "ToDoListFragment"
        private const val SIS_PRIORITY = "sis_priority"
        private const val SIS_FILTER = "sis_filter"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_to_do_list

    private val viewModel: ToDoListViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by sharedViewModel()

    private lateinit var fastAdapter: FastAdapter<TaskItem>
    private lateinit var itemAdapter: ItemAdapter<TaskItem>

    private lateinit var touchCallback: SimpleSwipeCallback
    private lateinit var touchHelper: ItemTouchHelper

    private var cachedPriority: Priority? = null
    private var cachedFilter: Filter = Filter.ALL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.apply {
            cachedFilter = Filter.valueOf(get(SIS_FILTER) as String)
            cachedPriority = Priority.valueOf(get(SIS_PRIORITY) as String)
        }
        radioBtnAll.isChecked = true
        initAdapter()
        viewModel.getToDoListBy()

        radioGroupPrioritySort.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            val cachedPriority = when (checkedId) {
                R.id.radioBtnAll -> null
                R.id.radioBtnLow -> Priority.LOW
                R.id.radioBtnMid -> Priority.MID
                R.id.radioBtnHigh -> Priority.HIGH
                else -> null
            }
            viewModel.setPriority(cachedPriority)
        }

        subscribeUi()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SIS_PRIORITY, cachedPriority?.name)
        outState.putString(SIS_FILTER, cachedFilter.name)
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
            ContextCompat.getColor(requireContext(), R.color.md_red_900)
        )
            .withBackgroundSwipeRight(ContextCompat.getColor(requireContext(), R.color.md_blue_900))
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
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        itemAdapter.clear()
                        if (result.data?.isEmpty()!!) {
                            msgEmptyList.visibility = View.VISIBLE
                        } else {
                            msgEmptyList.visibility = View.GONE
                            updateFastAdapterTaskData(result.data)
                        }
                        loading.visibility = View.GONE
                    }
                    RxStatus.ERROR -> {
                        loading.visibility = View.GONE
                        msgEmptyList.visibility = View.VISIBLE
                        sharedViewModel.showError(result.msg)
                    }
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                        msgEmptyList.visibility = View.GONE
                    }
                }

            })
            toDoList.observe(viewLifecycleOwner, Observer { taskList: List<Task> ->
                updateFastAdapterTaskData(taskList)
            })
            totalCount.observe(viewLifecycleOwner, Observer { count ->
                total.text = resources.getString(R.string.text_total, count)
            })
            completedCount.observe(viewLifecycleOwner, Observer { count ->
                completed.text = resources.getString(R.string.text_completed, count)
            })
            archivedCount.observe(viewLifecycleOwner, Observer { count ->
                archived.text = resources.getString(R.string.text_archived, count)
            })
            cachedFilterTransformations.observe(viewLifecycleOwner, Observer { })
            cachedPriorityTransformations.observe(viewLifecycleOwner, Observer { })
        }

        sharedViewModel.apply {

            filterLiveData.observe(viewLifecycleOwner, Observer { event: Event<Filter> ->
                val filter = event.getContentIfNotHandled()
                if (filter != null) {
                    cachedFilter = filter
                    viewModel.setFilter(filter)
                }
            })

            errorRetryLiveData.observe(viewLifecycleOwner, Observer {
                viewModel.getToDoListBy()
            })
        }
    }

    private fun updateFastAdapterTaskData(taskList: List<Task>) {
        val items = ArrayList<TaskItem>()
        taskList
            .forEach { task: Task ->
                items.add(
                    TaskItem(
                        task.id,
                        task.description,
                        task.date,
                        task.category?.categoryName!!,
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
                    ItemTouchHelper.LEFT -> viewModel.deleteTask(item.id)
                    ItemTouchHelper.RIGHT -> viewModel.archiveTask(item.id)
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