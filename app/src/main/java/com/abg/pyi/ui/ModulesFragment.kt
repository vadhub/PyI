package com.abg.pyi.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abg.pyi.ui.ActivityGraphFragment
import com.abg.pyi.data.DataProvider
import com.abg.pyi.MainActivity
import com.abg.pyi.adapters.ModulesAdapter
import com.abg.pyi.R
import com.abg.pyi.databinding.FragmentModulesBinding

class ModulesFragment : Fragment() {

    private var _binding: FragmentModulesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.calendar_container, ActivityGraphFragment())
                .commit()
        }

        val modules = DataProvider.getModules(requireContext())
        val sharedPref = requireContext().getSharedPreferences("test_results", Context.MODE_PRIVATE)
        val adapter = ModulesAdapter(modules, sharedPref) { module ->
            (activity as? MainActivity)?.navigateToModule(module.id)
        }

        binding.recyclerModules.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerModules.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ModulesFragment()
    }
}