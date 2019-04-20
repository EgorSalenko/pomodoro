package io.esalenko.pomadoro.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.fragment.NewTaskFragment
import io.esalenko.pomadoro.ui.fragment.ToDoListFragment
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    override val layoutRes: Int
        get() = R.layout.activity_main

    private val sharedViewModel: SharedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment == null) {
            ToDoListFragment().replace(R.id.fragmentContainer, ToDoListFragment.TAG)
        }

        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu)
            setOnMenuItemClickListener { item: MenuItem ->
                onMenuItemClicked(item)
            }
        }

        addTaskButton.setOnClickListener {
            NewTaskFragment().replace(R.id.overlayFragmentContainer, NewTaskFragment.TAG)
        }
        subscribeUi()
    }

    private fun subscribeUi() {
        sharedViewModel.apply {
            mainScreenLiveData.observe(this@MainActivity, Observer { event: Event<String> ->
                if (!event.hasBeenHandled) {
                    supportFragmentManager.findFragmentByTag(NewTaskFragment.TAG)?.remove()
                    ToDoListFragment().replace(R.id.fragmentContainer, ToDoListFragment.TAG)
                }
            })
        }
    }

    private fun onMenuItemClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                true
            }
            R.id.menu_profile -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
