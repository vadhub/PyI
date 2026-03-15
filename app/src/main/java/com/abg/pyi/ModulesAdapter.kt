package com.abg.pyi

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abg.pyi.databinding.ItemModuleBinding


class ModulesAdapter(
    private val modules: List<Module>,
    private val sharedPref: SharedPreferences,
    private val onItemClick: (Module) -> Unit
) : RecyclerView.Adapter<ModulesAdapter.ModuleViewHolder>() {

    class ModuleViewHolder(val binding: ItemModuleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.binding.tvModuleTitle.text = module.title

        if (module.id == 0 || module.id == 10) {
            holder.binding.tvProgress.visibility = View.GONE
        } else {
            holder.binding.tvProgress.visibility = View.VISIBLE
            val lessons = module.lessons
            var passedCount = 0
            for (lesson in lessons) {
                if (sharedPref.getBoolean("test_passed_${module.id}_${lesson.id}", false)) {
                    passedCount++
                }
            }
            holder.binding.tvProgress.text = "$passedCount/${lessons.size} тестов пройдено"
        }

        holder.binding.root.setOnClickListener { onItemClick(module) }
    }

    override fun getItemCount() = modules.size
}