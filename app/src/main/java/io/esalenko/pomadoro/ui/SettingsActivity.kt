package io.esalenko.pomadoro.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.get
import java.util.concurrent.TimeUnit


class SettingsActivity : BaseActivity() {

    private var sharedPreferenceManager: SharedPreferenceManager = get()

    private lateinit var workTimerDuration: String
    private lateinit var shortPauseDuration: String
    private lateinit var longPauseDuration: String

    override val layoutRes: Int
        get() = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createArrayAdapter(R.array.work_timer_duration, spinner_fragment_settings_work_timer)
        createArrayAdapter(R.array.short_pause_duration, spinner_fragment_settings_short_pause_timer)
        createArrayAdapter(R.array.long_pause_duration, spinner_fragment_settings_long_pause_timer)

        setupClickListeners()
    }

    private fun setupClickListeners() {

        spinner_fragment_settings_work_timer.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    workTimerDuration = parent?.getItemAtPosition(position) as String
                }

            }

        spinner_fragment_settings_short_pause_timer.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    shortPauseDuration = parent?.getItemAtPosition(position) as String
                }

            }

        spinner_fragment_settings_long_pause_timer.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    longPauseDuration = parent?.getItemAtPosition(position) as String
                }

            }

        fab_activity_settings_save.setOnClickListener {
            if (workTimerDuration != null){
                sharedPreferenceManager.timerDuration = TimeUnit.MINUTES.toMillis(workTimerDuration.toLong())
            }
            if (shortPauseDuration != null){
                sharedPreferenceManager.shortCooldownDuration = TimeUnit.MINUTES.toMillis(shortPauseDuration.toLong())
            }
            if (longPauseDuration != null){
                sharedPreferenceManager.longCooldownDuration = TimeUnit.MINUTES.toMillis(longPauseDuration.toLong())
            }
        }
    }

    companion object {
        const val TAG = "SettingsActivity"

        @JvmStatic
        fun Context.createSettingsActivityIntent(): Intent {
            return Intent(this, SettingsActivity::class.java)
        }
    }

    private fun createArrayAdapter(@ArrayRes arrayResource: Int, spinner: Spinner) {

        ArrayAdapter.createFromResource(
            this,
            arrayResource,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }
    }
}