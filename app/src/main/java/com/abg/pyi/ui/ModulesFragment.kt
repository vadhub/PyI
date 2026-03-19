package com.abg.pyi.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abg.pyi.MainActivity
import com.abg.pyi.adapters.ModulesAdapter
import com.abg.pyi.data.DataProvider
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

        val modules = DataProvider.getModules(requireContext())
        val sharedPref = requireContext().getSharedPreferences("test_results", Context.MODE_PRIVATE)
        val adapter = ModulesAdapter(modules, sharedPref) { module ->
            (activity as? MainActivity)?.navigateToModule(module.id)
        }

        binding.recyclerModules.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerModules.adapter = adapter
    }

    private fun updateOverallProgress() {
        val sharedPref = requireContext().getSharedPreferences("test_results", Context.MODE_PRIVATE)
        val modules = DataProvider.getModules(requireContext())
        var totalLessons = 0
        var passedTests = 0

        for (module in modules) {
            // Пропускаем модуль 0 и 10, если они существуют (по желанию)
            if (module.id == 0 || module.id == 10) continue

            val lessons = module.lessons
            totalLessons += lessons.size
            for (lesson in lessons) {
                if (sharedPref.getBoolean("test_passed_${module.id}_${lesson.id}", false)) {
                    passedTests++
                }
            }
        }

        val percent = if (totalLessons > 0) (passedTests * 100 / totalLessons) else 0
        binding.circularProgress.progress = percent
        binding.tvProgressPercent.text = "$percent%"
        binding.tvProgressDetail.text = "$passedTests/$totalLessons тестов"
    }

    override fun onResume() {
        super.onResume()
        updateOverallProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ModulesFragment()
    }
}