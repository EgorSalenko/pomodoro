package io.esalenko.pomadoro.ui.activity

import android.animation.Animator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.FilterType
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.common.animation.AnimatorListenerAdapter
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

    private var isNewTaskOpened: Boolean = false

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
            animationOpenNewTask()
            NewTaskFragment().add(R.id.overlayFragmentContainer, NewTaskFragment.TAG)
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
                animationOpenNewTask()
                val newTaskFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.overlayFragmentContainer)
                newTaskFragment?.remove()
            })
            errorLiveData.observe(this@MainActivity, Observer { event: Event<String> ->
                Snackbar.make(coordinatorLayout, event.getContentIfNotHandled().toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAnchorView(addTaskButton)
                    .setAction(R.string.snackbar_action_retry) {
                        sharedViewModel.onErrorRetry()
                    }
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
                }
                R.id.filter_by_date -> {
                    sharedViewModel.setFilter(FilterType.BY_DATE)
                }
                R.id.filter_by_archived -> {
                    sharedViewModel.setFilter(FilterType.BY_ARCHIVED)
                }
                R.id.filter_by_completed -> {
                    sharedViewModel.setFilter(FilterType.BY_COMPLETED)
                }
            }
            true
        }
        popupMenu.show()
    }

    override fun onBackPressed() {
        val newTaskFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.overlayFragmentContainer)
        if (newTaskFragment is NewTaskFragment) {
            animationOpenNewTask()
            newTaskFragment.remove()
        } else {
            super.onBackPressed()
        }
    }

    private fun animationOpenNewTask() {
        if (!isNewTaskOpened) {

            val x = addTaskButton.x.toInt() + addTaskButton.width / 2
            val y = addTaskButton.y.toInt() + addTaskButton.height / 2

            val startRadius = 0.0f
            val endRadius = (Math.hypot(
                overlayFragmentContainer.width.toDouble(),
                overlayFragmentContainer.height.toDouble()
            )).toFloat()

            val anim =
                ViewAnimationUtils.createCircularReveal(overlayFragmentContainer, x, y, startRadius, endRadius).apply {
                    interpolator = FastOutLinearInInterpolator()
                    duration = 800
                    addListener(object : AnimatorListenerAdapter {
                        override fun onAnimationStart(animation: Animator?) {
                            overlayFragmentContainer.visibility = View.VISIBLE
                        }
                    })
                }

            anim.start()
            isNewTaskOpened = true
        } else {

            val x = addTaskButton.x.toInt() + addTaskButton.width / 2
            val y = addTaskButton.y.toInt() + addTaskButton.height / 2

            val startRadius = (Math.hypot(
                overlayFragmentContainer.width.toDouble(),
                overlayFragmentContainer.height.toDouble()
            )).toFloat()
            val endRadius = 0.0f

            val anim =
                ViewAnimationUtils.createCircularReveal(overlayFragmentContainer, x, y, startRadius, endRadius).apply {
                    interpolator = FastOutLinearInInterpolator()
                    duration = 500
                }
            anim.addListener(object : AnimatorListenerAdapter {
                override fun onAnimationEnd(animation: Animator?) {
                    overlayFragmentContainer.visibility = View.INVISIBLE
                }
            })

            anim.start()
            isNewTaskOpened = false
        }
    }
}
