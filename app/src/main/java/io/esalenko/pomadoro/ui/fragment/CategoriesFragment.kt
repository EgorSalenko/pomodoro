package io.esalenko.pomadoro.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.task.Category
import io.esalenko.pomadoro.ui.adapter.CategoryItem
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.vm.ToDoListViewModel
import kotlinx.android.synthetic.main.fragment_categories.*
import org.jetbrains.anko.find
import org.koin.androidx.viewmodel.ext.android.viewModel


class CategoriesFragment : BaseFragment() {

    private val toDoListViewModel: ToDoListViewModel by viewModel()

    private lateinit var fastAdapter: FastAdapter<CategoryItem>
    private lateinit var itemAdapter: ItemAdapter<CategoryItem>

    override val layoutRes: Int
        get() = R.layout.fragment_categories

    companion object {
        const val TAG = "CategoriesFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        toDoListViewModel.getCategories()
        subscribeUi()
    }

    private fun subscribeUi() {
        toDoListViewModel.apply {
            // TODO :: Migrate to pure live data model
            categoryLiveData.observe(viewLifecycleOwner, Observer { result: RxResult<List<Category>> ->
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        loading.visibility = View.GONE

                        if (result.data?.size == 0) {
                            msgEmptyList.visibility = View.VISIBLE
                            return@Observer
                        }

                        msgEmptyList.visibility = View.GONE
                        val items = ArrayList<CategoryItem>()
                        result.data
                            ?.forEach { category ->
                                items.add(CategoryItem(category))
                            }

                        FastAdapterDiffUtil.set(itemAdapter, items)
                    }
                    RxStatus.ERROR -> {
                        loading.visibility = View.GONE
                        msgEmptyList.visibility = View.GONE
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    RxStatus.LOADING -> {
                        msgEmptyList.visibility = View.GONE
                        loading.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun initRecyclerView() {
        itemAdapter = ItemAdapter.items()
        fastAdapter = FastAdapter.with(itemAdapter)
        recyclerCategories.itemAnimator = SlideDownAlphaAnimator()
        recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        recyclerCategories.adapter = fastAdapter

        fastAdapter.withEventHook(object : ClickEventHook<CategoryItem>() {

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                if (viewHolder is CategoryItem.CategoryItemViewHolder) {
                    return viewHolder.itemView.find<ImageView>(R.id.iconRemove)
                }
                return null
            }

            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<CategoryItem>, item: CategoryItem) {
                toDoListViewModel.apply {
                    deleteCategory(item.category)
                    getCategories()
                }
            }
        })
    }
}