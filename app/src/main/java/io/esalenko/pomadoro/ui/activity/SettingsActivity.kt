package io.esalenko.pomadoro.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.fragment.CategoriesFragment
import io.esalenko.pomadoro.ui.fragment.SettingsFragment
import io.esalenko.pomadoro.vm.SharedSettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : BaseActivity() {

    private val sharedSettingsViewModel: SharedSettingsViewModel by viewModel()

    override val layoutRes: Int
        get() = R.layout.activity_settings

    companion object {
        fun Context.createSettingsActivityIntent(): Intent {
            return Intent(this, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            SettingsFragment().replace(R.id.fragmentContainer, SettingsFragment.TAG)
        }
        setupToolbar()
        subscribeUi()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.menu_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }

    private fun subscribeUi() {
        sharedSettingsViewModel.apply {
            openCategoriesEvent.observe(this@SettingsActivity, Observer {
                CategoriesFragment().replace(R.id.fragmentContainer, CategoriesFragment.TAG)
            })
        }
    }

    override fun onBackPressed() {
        when (getLastFragment()) {
            is SettingsFragment -> super.onBackPressed()
            is CategoriesFragment -> {
                SettingsFragment().replace(R.id.fragmentContainer, SettingsFragment.TAG)
            }
            else -> super.onBackPressed()
        }

    }
}