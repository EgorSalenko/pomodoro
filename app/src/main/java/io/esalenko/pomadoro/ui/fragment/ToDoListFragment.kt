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
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeDragCallback
import com.mikepenz.fastadapter_extensions.utilities.DragDropUtil
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.FilterType
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.ui.adapter.TaskItem
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.ToDoListVIewModel
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import org.jetbrains.anko.info
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ToDoListFragment : BaseFragment(), ItemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback {

    companion object {
        const val TAG = "ToDoListFragment"
    }

    override val layoutRes: Int
        get() = R.layout.fragment_to_do_list

    private val viewModel: ToDoListVIewModel by viewModel()
    private val sharedViewModel: SharedViewModel by sharedViewModel()

    private lateinit var fastAdapter: FastAdapter<TaskItem>
    private lateinit var itemAdapter: ItemAdapter<TaskItem>

    //drag & drop
    private lateinit var touchCallback: SimpleDragCallback
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

        val leaveBehindDrawableLeft = context?.getDrawable(R.drawable.ic_round_clear_24px)

        val leaveBehindDrawableRight = context?.getDrawable(R.drawable.ic_round_save_24px)

        touchCallback = SimpleSwipeDragCallback(
            this,
            this,
            leaveBehindDrawableLeft,
            ItemTouchHelper.LEFT,
            ContextCompat.getColor(context!!, R.color.md_red_900)
        )
            .withBackgroundSwipeRight(ContextCompat.getColor(context!!, R.color.md_blue_900))
            .withLeaveBehindSwipeRight(leaveBehindDrawableRight)

        touchHelper =
            ItemTouchHelper(touchCallback) // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(toDoList) // Attach ItemTouchHelper to RecyclerView

    }

    private fun subscribeUi() {

        viewModel.apply {

            toDoListLiveData.observe(viewLifecycleOwner, Observer { result: RxResult<List<Task>> ->
                val items = ArrayList<TaskItem>()
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        itemAdapter.clear()
                        result
                            .data
                            ?.forEach { task: Task ->
                                items.add(
                                    TaskItem(
                                        task.id,
                                        task.description,
                                        task.date,
                                        task.category.categoryName,
                                        task.priority
                                    )
                                )
                                info { task }
                            }
                        FastAdapterDiffUtil.set(itemAdapter, items)
                        loading.visibility = View.GONE
                    }
                    RxStatus.ERROR -> {
                        loading.visibility = View.GONE
                        sharedViewModel.showError(result.msg)
                    }
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                    }
                }

            })

        }

        sharedViewModel.apply {

            filterLiveData.observe(viewLifecycleOwner, Observer { event: Event<FilterType> ->
                when (event.getContentIfNotHandled()) {
                    FilterType.BY_PRIORITY -> {
                        viewModel.getToDoListByPriority()
                    }
                    FilterType.BY_DATE -> {
                        viewModel.getToDoListLatest()
                    }
                    FilterType.BY_ARCHIVED -> {
                        viewModel.getToDoListArchived()
                    }
                }
            })

            errorRetryLiveData.observe(viewLifecycleOwner, Observer {
                viewModel.fetchToDoList()
            })
        }
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(itemAdapter, oldPosition, newPosition)  // change position
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        // save the new item order, i.e. in your database
        val oldItem: TaskItem = itemAdapter.getAdapterItem(oldPosition)
        val newItem: TaskItem = itemAdapter.getAdapterItem(newPosition)

        viewModel.exchangeItems(oldItem.id, newItem.id)
    }

    override fun itemSwiped(position: Int, direction: Int) {
        // -- Option 1: Direct action --
        //do something when swiped such as: select, remove, update, ...:
        //A) fastItemAdapter.select(position);
        //B) fastItemAdapter.remove(position);
        //C) update item, set "read" if an email etc

        // -- Option 2: Delayed action --
        val item = itemAdapter.getAdapterItem(position)
        item.swipedDirection = direction

        // This can vary depending on direction but remove & archive simulated here both results in
        // removal from list
        val removeRunnable = Runnable {
            item.swipedAction = null
            val _position: Int = itemAdapter.getAdapterPosition(item)
            if (_position != RecyclerView.NO_POSITION) {
                //this sample uses a filter. If a filter is used we should use the methods provided by the filter (to make sure filter and normal state is updated)
                itemAdapter.remove(_position)

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