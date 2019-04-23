package io.esalenko.pomadoro.ui.activity

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.FilterType
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.fragment.NewTaskFragment
import io.esalenko.pomadoro.ui.fragment.ToDoListFragment
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    override val layoutRes: Int
        get() = R.layout.activity_main

    private val sharedViewModel: SharedViewModel by viewModel()

    private lateinit var popupMenu: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState.let {
            ToDoListFragment().replace(R.id.fragmentContainer, ToDoListFragment.TAG)
        }

        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu)
            setOnMenuItemClickListener { item: MenuItem ->
                onMenuItemClicked(item)
            }
        }
        addTaskButton.setOnClickListener {
            NewTaskFragment().replace(R.id.overlayFragmentContainer, NewTaskFragment.TAG, addToBackStack = true)
        }
        setupPopUp()
        subscribeUi()
    }

    private fun setupPopUp() {
        popupMenu = PopupMenu(this, bottomAppBar.find(R.id.menu_filter))
        popupMenu.inflate(R.menu.filter_popup_menu)

    }

    private fun subscribeUi() {
        sharedViewModel.apply {
            mainScreenLiveData.observe(this@MainActivity, Observer { event: Event<String> ->
                if (!event.hasBeenHandled) {
                    supportFragmentManager.findFragmentByTag(NewTaskFragment.TAG)?.remove()
                    ToDoListFragment().replace(R.id.fragmentContainer, ToDoListFragment.TAG)
                }
            })
            errorLiveData.observe(this@MainActivity, Observer { event: Event<String> ->
                Snackbar.make(coordinatorLayout, event.getContentIfNotHandled().toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAnchorView(addTaskButton)
                    .show()
            })
        }
    }

    private fun onMenuItemClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                openFilterPopUp()
                true
            }
            R.id.menu_profile -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFilterPopUp() {
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.filter_by_priority -> {
                    sharedViewModel.setFilter(FilterType.BY_PRIORITY)
                    true
                }
                R.id.filter_by_date -> {
                    sharedViewModel.setFilter(FilterType.BY_DATE)
                    true
                }
                R.id.filter_by_archived -> {
                    sharedViewModel.setFilter(FilterType.BY_ARCHIVED)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
    }
}