package com.abg.pyi.adapters

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abg.pyi.models.CalendarDay
import com.abg.pyi.databinding.ItemCalendarCellBinding

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var days: List<CalendarDay> = emptyList()

    fun submitList(list: List<CalendarDay>) {
        days = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    class CalendarViewHolder(private val binding: ItemCalendarCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            val colorRes = when (day.colorLevel) {
                0 -> R.color.transparent
                1 -> com.abg.pyi.R.color.activity_low
                2 -> com.abg.pyi.R.color.activity_medium
                3 -> com.abg.pyi.R.color.activity_high
                4 -> com.abg.pyi.R.color.activity_very_high
                else -> R.color.transparent
            }
            binding.root.setBackgroundColor(binding.root.context.getColor(colorRes))
            binding.root.contentDescription = "${day.date}: ${day.count} действий"
        }
    }
}