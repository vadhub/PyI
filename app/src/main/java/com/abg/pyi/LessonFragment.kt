package com.abg.pyi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.abg.pyi.code_editor.CodeEditorHelper
import com.abg.pyi.code_editor.ICodeEditorActions
import com.abg.pyi.databinding.FragmentLessonBinding
import com.amrdeveloper.codeview.CodeView

class LessonFragment : Fragment(), ICodeEditorActions {

    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeEditorHelper: CodeEditorHelper
    private lateinit var codeView: CodeView
    private lateinit var tvResult: TextView

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

        codeView = binding.editTextCode
        tvResult = binding.textViewResult

        // Инициализируем helper, передавая TextView для языка и позиции (они есть в разметке)
        codeEditorHelper = CodeEditorHelper(
            codeView = codeView,
            context = requireContext(),
            languageNameTextView = binding.languageNameTxt,
            sourcePositionTextView = binding.sourcePositionTxt
        )
        codeEditorHelper.setup("Python")

        // Загружаем данные урока
        val module = DataProvider.getModules().find { it.id == moduleId }
        val lesson = module?.lessons?.find { it.id == lessonId }

        if (lesson != null) {
            binding.tvTheory.text = lesson.theory
            codeEditorHelper.setInitialCode(lesson.initialCode)
        }

        binding.buttonRun.setOnClickListener {
            val code = codeEditorHelper.getCode()
            val result = codeEditorHelper.executePythonCode(code)
            tvResult.text = result
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Реализация интерфейса ICodeEditorActions
    override fun undo() = codeEditorHelper.undo()
    override fun redo() = codeEditorHelper.redo()
    override fun toggleComment() = codeEditorHelper.commentSelected()
    override fun uncomment() = codeEditorHelper.unCommentSelected()
    override fun clearText() = codeEditorHelper.clearText()
    override fun findAndReplace() {
        (requireActivity() as? MainActivity)?.let {
            codeEditorHelper.showFindAndReplaceDialog(it)
        }
    }
    override fun changeTheme(themeId: Int) = codeEditorHelper.changeTheme(themeId)
    override fun toggleRelativeLineNumber() = codeEditorHelper.toggleRelativeLineNumber()

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