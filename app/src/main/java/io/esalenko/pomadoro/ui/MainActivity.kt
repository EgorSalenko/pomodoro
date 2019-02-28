package io.esalenko.pomadoro.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.vm.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var isTimerRunning: Boolean = false

    private lateinit var sharedViewModel: SharedViewModel

    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fab_activity_main.setImageResource(if (!isTimerRunning) R.drawable.ic_round_timer_24px else R.drawable.ic_round_stop_24px)

        sharedViewModel = getSharedViewModel(SharedViewModel::class)

        TimerFragment().replace(R.id.fl_activity_main_fragment_container, TimerFragment.TAG)

        fab_activity_main.setOnClickListener {
            sharedViewModel.setActivatedState(!isTimerRunning)
        }

        subscribeUi()
    }

    private fun subscribeUi() {
        sharedViewModel.isActivatedLiveData.observe(this, Observer { isActivated: Boolean ->
            isTimerRunning = isActivated
            fab_activity_main.setImageResource(if (!isTimerRunning) R.drawable.ic_round_timer_24px else R.drawable.ic_round_stop_24px)
        })
    }


}
