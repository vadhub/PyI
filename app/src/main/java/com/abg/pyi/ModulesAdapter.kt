package com.abg.pyi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abg.pyi.databinding.ItemModuleBinding


class ModulesAdapter(
    private val modules: List<Module>,
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
        holder.binding.root.setOnClickListener { onItemClick(module) }
    }

    override fun getItemCount() = modules.size
}