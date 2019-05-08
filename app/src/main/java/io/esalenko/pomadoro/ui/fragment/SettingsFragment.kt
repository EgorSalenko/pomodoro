package io.esalenko.pomadoro.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.ui.common.BaseFragment
import io.esalenko.pomadoro.vm.SettingsViewModel
import io.esalenko.pomadoro.vm.SharedSettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class SettingsFragment : BaseFragment() {

    private val settingsViewModel: SettingsViewModel by viewModel()
    private val sharedSettingsViewModel: SharedSettingsViewModel by sharedViewModel()

    override val layoutRes: Int
        get() = R.layout.fragment_settings

    companion object {
        const val TAG = "SettingsFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnChangeListeners()
        btnCategories.setOnClickListener {
            sharedSettingsViewModel.openCategories()
        }
        btnLicenses.setOnClickListener {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.text_licenses))
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }
        btnClearAllData.setOnClickListener {
            showClearAllDataDialog()
        }
        subscribeUi()
    }

    private fun showClearAllDataDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_clear_data)
            message(R.string.dialog_message_clear_data)
            negativeButton()
            positiveButton {
                settingsViewModel.clearAllData()
            }
        }
    }

    private fun setOnChangeListeners() {
        seekbarTimerWork.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setWorkTimerDuration(
                        TimeUnit.MINUTES.toMillis(
                            seekBar?.progress?.toLong() ?: SharedPreferenceManager.TIMER_DURATION_IN_MINUTES
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        seekbarTimerPause.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setPauseTimerDuration(
                        TimeUnit.MINUTES.toMillis(
                            seekBar?.progress?.toLong() ?: SharedPreferenceManager.COOLDOWN_DURATION_IN_MINUTES
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun subscribeUi() {
        settingsViewModel.apply {
            workTimerLiveData.observe(viewLifecycleOwner, Observer { workTimerDuration ->
                val minutes = TimeUnit.MILLISECONDS.toMinutes(workTimerDuration)
                seekbarTimerWork.progress = minutes.toInt()
                textTimerWork.text =
                    getString(R.string.text_work_timer, minutes)
            })
            pauseTimerLiveData.observe(viewLifecycleOwner, Observer { pauseTimerDuration ->
                val minutes = TimeUnit.MILLISECONDS.toMinutes(pauseTimerDuration)
                seekbarTimerPause.progress = minutes.toInt()
                textTimerPause.text =
                    getString(R.string.text_pause_timer, minutes)
            })
        }
    }

}