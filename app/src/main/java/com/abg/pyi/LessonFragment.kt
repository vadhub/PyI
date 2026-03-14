package com.abg.pyi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abg.pyi.databinding.FragmentLessonBinding


class LessonFragment : Fragment() {

    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!

    private var moduleId: Int = 0
    private var lessonId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            moduleId = it.getInt(ARG_MODULE_ID)
            lessonId = it.getInt(ARG_LESSON_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val module = DataProvider.getModules().find { it.id == moduleId }
        val lesson = module?.lessons?.find { it.id == lessonId }

        if (lesson != null) {
            binding.tvTheory.text = lesson.theory
            binding.etCode.setText(lesson.initialCode)
        }

        binding.btnRun.setOnClickListener {
            val code = binding.etCode.text.toString()
            val output = executePythonCode(code)
            binding.tvOutput.text = output
        }
    }

    private fun executePythonCode(code: String): String {
        val result = StringBuilder()
        val regex = """print\s*\(\s*["']([^"']+)["']\s*\)""".toRegex()
        val matches = regex.findAll(code)
        for (match in matches) {
            result.append(match.groupValues[1]).append("\n")
        }
        if (result.isEmpty()) {

            val numberRegex = """print\s*\(\s*(\d+)\s*\)""".toRegex()
            val numberMatches = numberRegex.findAll(code)
            for (match in numberMatches) {
                result.append(match.groupValues[1]).append("\n")
            }
        }
        return if (result.isNotEmpty()) result.toString() else "Выполнено (нет вывода)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODULE_ID = "module_id"
        private const val ARG_LESSON_ID = "lesson_id"

        fun newInstance(moduleId: Int, lessonId: Int) = LessonFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MODULE_ID, moduleId)
                putInt(ARG_LESSON_ID, lessonId)
            }
        }
    }
}