package com.abg.pyi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.abg.pyi.databinding.FragmentLessonsPagerBinding

class LessonsPagerFragment : Fragment() {

    private var _binding: FragmentLessonsPagerBinding? = null
    private val binding get() = _binding!!

    private var moduleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            moduleId = it.getInt(ARG_MODULE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modules = DataProvider.getModules(requireContext())
        val module = modules.find { it.id == moduleId } ?: return

        val adapter = LessonsPagerAdapter(this, module)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = module.lessons[position].title
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODULE_ID = "module_id"

        fun newInstance(moduleId: Int) = LessonsPagerFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MODULE_ID, moduleId)
            }
        }
    }
}

class LessonsPagerAdapter(fragment: Fragment, private val module: Module) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = module.lessons.size

    override fun createFragment(position: Int): Fragment {
        val lesson = module.lessons[position]
        return LessonFragment.newInstance(module.id, lesson.id)
    }
}