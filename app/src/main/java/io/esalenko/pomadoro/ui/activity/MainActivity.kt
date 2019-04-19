package io.esalenko.pomadoro.ui.activity

import android.os.Bundle
import android.view.MenuItem
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu)
            setOnMenuItemClickListener { item ->
                onMenuItemClicked(item)
            }
        }

        addTaskButton.setOnClickListener {

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
