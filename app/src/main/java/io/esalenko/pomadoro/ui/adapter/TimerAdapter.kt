package io.esalenko.pomadoro.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.esalenko.pomadoro.R
import kotlinx.android.synthetic.main.layout_timer.view.*


class TimerAdapter(private val array: LongArray) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        return TimerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_timer, parent, false))
    }

    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bindView(array[position])
    }

    class TimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(timerValue: Long) {
            itemView.timer.text = timerValue.toString()
        }
    }
}