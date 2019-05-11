package io.esalenko.pomadoro.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.task.Category
import org.jetbrains.anko.find


class CategoryItem(val category: Category) : AbstractItem<CategoryItem, CategoryItem.CategoryItemViewHolder>() {

    override fun getType(): Int = R.id.fast_item_category

    override fun getViewHolder(v: View) = CategoryItemViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_category

    class CategoryItemViewHolder(v: View) : FastAdapter.ViewHolder<CategoryItem>(v) {

        val text = v.find<TextView>(R.id.textCategory)
        val icon = v.find<ImageView>(R.id.iconRemove)

        override fun bindView(item: CategoryItem, payloads: MutableList<Any>) {
            text.text = item.category.categoryName
            if (item.category.isDefault) {
                icon.isClickable = false
                icon.visibility = View.INVISIBLE
            }
        }

        override fun unbindView(item: CategoryItem) {

        }

    }

}