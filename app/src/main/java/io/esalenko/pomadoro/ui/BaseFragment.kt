package io.esalenko.pomadoro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.esalenko.pomadoro.di.Injectable


abstract class BaseFragment : Fragment(), Injectable {

    @get:LayoutRes
    protected abstract val layoutRes : Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutRes, container, false)
        return view
    }

}