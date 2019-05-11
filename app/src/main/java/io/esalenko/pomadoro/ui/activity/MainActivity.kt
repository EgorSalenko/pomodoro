package io.esalenko.pomadoro.ui.activity

import android.animation.Animator
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.Filter
import io.esalenko.pomadoro.db.model.TimerState
import io.esalenko.pomadoro.receiver.AlarmReceiver
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.activity.MainActivity.FragmentPage.*
import io.esalenko.pomadoro.ui.activity.SettingsActivity.Companion.createSettingsActivityIntent
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.common.animation.AnimatorListenerAdapter
import io.esalenko.pomadoro.ui.fragment.DetailTaskFragment
import io.esalenko.pomadoro.ui.fragment.NewTaskFragment
import io.esalenko.pomadoro.ui.fragment.ToDoListFragment
import io.esalenko.pomadoro.util.avoidDoubleClick
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.TimerViewModel
import io.esalenko.pomadoro.vm.TimerViewModel.TimerAction
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    override val layoutRes: Int
        get() = R.layout.activity_main

    private val sharedViewModel: SharedViewModel by viewModel()
    private val timerViewModel: TimerViewModel by viewModel()

    private var isNewTaskOpened: Boolean = false
    private var isCooldown: Boolean = false

    private var isBound: Boolean = false
    private var isCompletedTask: Boolean? = false
    private var taskId: Long = -1L

    private var fragmentPage: FragmentPage? = null

    private lateinit var popupMenu: PopupMenu
    private var countdownService: CountdownService? = null

    private var serviceConnection: ServiceConnection? = null

    override fun onStart() {
        super.onStart()
        startBoundService()
    }

    private fun startBoundService(fromTimer: Boolean = false) {
        createServiceConnection(fromTimer)
        startService(createCountdownServiceIntent())
        bindService(createCountdownServiceIntent(), serviceConnection, Context.BIND_IMPORTANT)
    }

    private fun createServiceConnection(fromTimer: Boolean) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val countdownBinder: CountdownService.CountdownBinder = service as CountdownService.CountdownBinder
                countdownService = countdownBinder.countdownService
                countdownService?.setCountdownCommunicationCallback(this@MainActivity)
                isBound = true
                if (fromTimer) {
                    countdownService?.startTimer(taskId, isCooldown)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                countdownService = null
                isBound = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
            serviceConnection = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extrasTaskId = intent?.extras?.get(AlarmReceiver.KEY_TASK_ID) as? Long ?: -1L
        isCompletedTask = intent?.extras?.get(AlarmReceiver.KEY_TASK_IS_COMPLETED) as? Boolean ?: false
        if (savedInstanceState == null) {
            if (extrasTaskId != -1L) {
                openDetailTaskFragment(extrasTaskId)
            } else {
                openToDoListFragment()
            }
        }

        fab.apply {
            setColorFilter(Color.WHITE)
            setOnClickListener {
                avoidDoubleClick {
                    when (fragmentPage) {
                        MAIN, NEW_TASK, null -> {
                            openNewTaskFragment()
                        }
                        DETAILED -> {
                            if (isCompletedTask == true) {
                                timerViewModel.restoreCompletedTask(taskId)
                                onBackPressed()
                            } else {
                                timerViewModel.saveLastStartedTaskId(-1)
                                timerViewModel.completeTask(taskId)
                                countdownService?.stopTimer(taskId)
                                onBackPressed()
                            }
                        }
                    }
                }
            }
        }
        subscribeUi()
    }

    override fun onTimerResult(timer: String) {
        timerViewModel.provideTimer(timer)
    }

    override fun onTimerStateChangeListener(timerState: TimerState) {
        timerViewModel.provideState(timerState)
    }


    private fun setupPopUp() {
        popupMenu = PopupMenu(this, bottomAppBar.find(R.id.menu_filter))
        popupMenu.inflate(R.menu.filter_popup_menu)
    }

    private fun subscribeUi() {
        sharedViewModel.apply {
            mainScreenLiveData
                .observe(
                    this@MainActivity,
                    Observer {
                        animationOpenNewTask()
                        val newTaskFragment: Fragment? =
                            supportFragmentManager.findFragmentById(R.id.overlayFragmentContainer)
                        newTaskFragment?.remove()
                    }
                )

            errorLiveData
                .observe(
                    this@MainActivity,
                    Observer { event: Event<String> ->
                        val message = event.getContentIfNotHandled().toString()
                        Snackbar.make(
                            coordinatorLayout,
                            message,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAnchorView(fab)
                            .setAction(R.string.snackbar_action_retry) {
                                sharedViewModel.onErrorRetry()
                            }
                            .show()
                    }
                )

            detailScreenEventLiveData
                .observe(
                    this@MainActivity,
                    Observer { event: Event<Pair<Long, Boolean>> ->
                        val pair = event.getContentIfNotHandled()
                        val id: Long = pair?.first ?: return@Observer
                        val isCompleted: Boolean = pair.second
                        this@MainActivity.isCompletedTask = isCompleted
                        openDetailTaskFragment(id)
                    })
        }

        timerViewModel.apply {

            shareTaskLiveData.observe(this@MainActivity, Observer { task ->
                taskId = task.id
                isCooldown = task.isCooldown
                isCompletedTask = task.isCompleted
            })

            timerActionLiveData.observe(this@MainActivity, Observer { event ->
                when (event.getContentIfNotHandled()) {
                    TimerAction.START -> {
                        // fckn hack bt i donno how to make it better rn
                        if (countdownService == null) {
                            startBoundService(true)
                        } else {
                            countdownService?.startTimer(taskId, isCooldown)
                        }
                    }
                    TimerAction.STOP -> countdownService?.stopTimer(taskId)
                }
            })
        }

    }

    private fun setupToolbar() {
        TransitionManager.beginDelayedTransition(mainLayout)
        toolbar.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        TransitionManager.endTransitions(mainLayout)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerViewModel.resetAllFilters()
    }

    private fun onMenuItemClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_filter -> {
                openFilterPopUp()
            }
            R.id.menu_settings -> {
                openSettingsActivity()
            }
            R.id.delete_item -> {
                avoidDoubleClick {
                    MaterialDialog(this).show {
                        title(R.string.delete_item)
                        message(R.string.delete_item_msg)
                        icon(R.drawable.ic_round_delete_forever_24px)
                        negativeButton {
                            it.dismiss()
                        }
                        positiveButton {
                            it.dismiss()
                            openToDoListFragment()
                            countdownService?.stopTimer(taskId)
                            timerViewModel.removeTask(taskId)
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSettingsActivity() {
        startActivity(createSettingsActivityIntent())
    }

    private fun openToDoListFragment() {
        toolbar.visibility = View.GONE
        ToDoListFragment().replace(R.id.fragmentContainer, ToDoListFragment.TAG)
        fragmentPage = MAIN
        setupBottomAppBar(MAIN)
        setupPopUp()
    }

    private fun openNewTaskFragment() {
        animationOpenNewTask()
        NewTaskFragment().add(R.id.overlayFragmentContainer, NewTaskFragment.TAG)
    }

    private fun openDetailTaskFragment(id: Long) {
        fragmentPage = DETAILED
        setupBottomAppBar(DETAILED)
        setupToolbar()
        DetailTaskFragment
            .newInstance(id)
            .replace(R.id.fragmentContainer, DetailTaskFragment.TAG)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openFilterPopUp() {
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.filter_by_all -> {
                    sharedViewModel.setFilter(Filter.ALL)
                }
                R.id.filter_by_archived -> {
                    sharedViewModel.setFilter(Filter.ARCHIVED)
                }
                R.id.filter_by_completed -> {
                    sharedViewModel.setFilter(Filter.COMPLETED)
                }
            }
            true
        }
        popupMenu.show()
    }

    override fun onBackPressed() {
        val lastFragment: Fragment? = getLastFragment()

        if (lastFragment == null) {
            super.onBackPressed()
        }

        when (lastFragment) {
            is NewTaskFragment -> {
                animationOpenNewTask()
                lastFragment.remove()
            }
            is DetailTaskFragment -> {
                openToDoListFragment()
            }
            else -> super.onBackPressed()
        }
    }

    private fun setupBottomAppBar(page: FragmentPage) {
        when (page) {
            MAIN, NEW_TASK -> {
                fab.apply {
                    setImageResource(R.drawable.ic_round_add_24px)
                    setColorFilter(Color.WHITE)
                }
                bottomAppBar.apply {
                    fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    replaceMenu(R.menu.bottom_app_bar_menu)
                    setOnMenuItemClickListener { item: MenuItem ->
                        onMenuItemClicked(item)
                    }
                }
                setupPopUp()
            }
            DETAILED -> {
                fab.apply {
                    setImageResource(if (isCompletedTask!!) R.drawable.ic_round_undo_24px else R.drawable.ic_round_done_24px)
                    setColorFilter(Color.WHITE)
                }
                bottomAppBar.apply {
                    fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    replaceMenu(R.menu.bottom_app_bar_menu_task)
                    setOnMenuItemClickListener { item: MenuItem ->
                        onMenuItemClicked(item)
                    }
                }
            }
        }
    }

    private fun animationOpenNewTask() {
        if (!isNewTaskOpened) {

            val x = fab.x.toInt() + fab.width / 2
            val y = fab.y.toInt() + fab.height / 2

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

            val x = fab.x.toInt() + fab.width / 2
            val y = fab.y.toInt() + fab.height / 2

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

    enum class FragmentPage {
        MAIN,
        NEW_TASK,
        DETAILED
    }
}
