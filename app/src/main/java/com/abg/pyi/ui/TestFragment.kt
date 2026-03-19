package com.abg.pyi.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.abg.pyi.data.DataProvider
import com.abg.pyi.MyApp
import com.abg.pyi.models.TestQuestion
import com.abg.pyi.databinding.FragmentTestBinding
import kotlinx.coroutines.launch

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!

    private var moduleId: Int = 0
    private var lessonId: Int = 0
    private var questions: List<TestQuestion> = emptyList()
    private var currentIndex = 0
    private val answers = mutableListOf<Int>()
    private lateinit var interstitialManager: InterstitialManager

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
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interstitialManager = InterstitialManager(requireActivity())
        interstitialManager.loadAd()

        questions = DataProvider.getTestQuestions(requireContext(), moduleId, lessonId)
        if (questions.isEmpty()) {
            Toast.makeText(requireContext(), "Тест для этого урока не найден", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        answers.clear()
        repeat(questions.size) { answers.add(-1) }

        showQuestion(0)
    }

    private fun showQuestion(index: Int) {
        if (index >= questions.size) {
            showResult()
            return
        }

        val q = questions[index]
        binding.tvProgress.text = "Вопрос ${index + 1}/${questions.size}"
        binding.tvQuestion.text = q.question

        val radioButtons = listOf(
            binding.radio0,
            binding.radio1,
            binding.radio2,
            binding.radio3
        )

        binding.radioGroup.clearCheck()
        for (i in radioButtons.indices) {
            if (i < q.options.size) {
                radioButtons[i].text = q.options[i]
                radioButtons[i].visibility = View.VISIBLE
                radioButtons[i].isChecked = false
            } else {
                radioButtons[i].visibility = View.GONE
            }
        }

        binding.btnNext.setOnClickListener {
            val selected = radioButtons.indexOfFirst { it.isChecked }
            if (selected == -1) {
                Toast.makeText(requireContext(), "Выберите ответ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            answers[index] = selected

            if (index + 1 < questions.size) {
                showQuestion(index + 1)
            } else {
                showResult()
            }
        }
    }

    private fun showResult() {
        var correctCount = 0
        for (i in questions.indices) {
            if (answers[i] == questions[i].correctIndex) {
                correctCount++
            }
        }
        val total = questions.size
        val passed = correctCount > total / 2

        val prefs = requireContext().getSharedPreferences("test_results", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("test_passed_${moduleId}_${lessonId}", passed).apply()

        val message = buildString {
            append("Результат: $correctCount/$total\n")
            if (passed) {
                append("Тест пройден!")
            } else {
                append("Тест не пройден. Попробуйте ещё раз.")
            }
            for (i in questions.indices) {
                if (answers[i] != questions[i].correctIndex) {
                    append("\n\nВопрос ${i + 1}: ${questions[i].question}")
                    append("\nПравильный ответ: ${questions[i].options[questions[i].correctIndex]}")
                    if (questions[i].explanation.isNotBlank()) {
                        append("\nПояснение: ${questions[i].explanation}")
                    }
                }
            }
        }

        binding.tvProgress.text = "Тест завершён"
        binding.tvQuestion.visibility = View.VISIBLE
        binding.tvQuestion.text = message
        binding.radioGroup.visibility = View.GONE
        binding.btnNext.text = "Закрыть"
        binding.btnNext.setOnClickListener {
            interstitialManager.showAd {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODULE_ID = "module_id"
        private const val ARG_LESSON_ID = "lesson_id"

        fun newInstance(moduleId: Int, lessonId: Int) = TestFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MODULE_ID, moduleId)
                putInt(ARG_LESSON_ID, lessonId)
            }
        }
    }
}