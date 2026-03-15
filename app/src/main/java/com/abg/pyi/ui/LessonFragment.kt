package com.abg.pyi.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.abg.pyi.data.DataProvider
import com.abg.pyi.MyApp
import com.abg.pyi.R
import com.abg.pyi.editor.CodeEditorHelper
import com.abg.pyi.editor.ICodeEditorActions
import com.abg.pyi.databinding.FragmentLessonBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LessonFragment : Fragment(), ICodeEditorActions {

    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!

    private var isHorizontalScroll = false
    private lateinit var gestureDetector: GestureDetector

    private lateinit var codeEditorHelper: CodeEditorHelper

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeEditorHelper = CodeEditorHelper(binding.editTextCode, requireContext())
        codeEditorHelper.setup()

        val isModule0or8or10 = moduleId == 0 || moduleId == 8 || moduleId == 10

        if (isModule0or8or10) {
            binding.btnRun.visibility = View.GONE
            binding.editTextInput.visibility = View.GONE
            binding.tvOutput.visibility = View.GONE
            binding.tvInput.visibility = View.GONE
            binding.tvOut.visibility = View.GONE
            binding.editTextCode.visibility = View.GONE
            binding.sourceInfoLayout.visibility = View.GONE
            binding.tvCodeLabel.visibility = View.GONE
            if (moduleId != 0) {
                binding.tvCodeLabel.visibility = View.VISIBLE
                binding.sourceInfoLayout.visibility = View.VISIBLE
                binding.editTextCode.visibility = View.VISIBLE
                binding.btnCopy.visibility = View.VISIBLE
                binding.btnCopy.setOnClickListener {
                    val code = binding.editTextCode.text.toString()
                    val clipboard =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Python code", code)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(requireContext(), "Код скопирован", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            binding.btnRun.visibility = View.VISIBLE
            binding.editTextInput.visibility = View.VISIBLE
            binding.tvOutput.visibility = View.VISIBLE
            binding.btnCopy.visibility = View.GONE
        }

        val module = DataProvider.getModules(requireContext()).find { it.id == moduleId }
        val lesson = module?.lessons?.find { it.id == lessonId }

        if (lesson != null) {
            val webView = binding.wvTheory
            webView.settings.javaScriptEnabled = false
            webView.settings.setSupportZoom(false)
            webView.isVerticalScrollBarEnabled = false

            val htmlContent = """
        <html>
        <head>
            <style>
                body { 
                    font-family: monospace; 
                    font-size: 16px;
                    line-height: 1.4;
                }
                pre { 
                    background-color: #f5f5f5; 
                    padding: 8px; 
                    border-radius: 4px;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                }
                code { 
                    font-family: monospace; 
                }
                h2 { font-size: 20px; font-weight: bold; margin-top: 16px; }
                h3 { font-size: 18px; font-weight: bold; margin-top: 12px; }
            </style>
        </head>
        <body>
            ${lesson.theory}
        </body>
        </html>
    """.trimIndent()
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

            binding.editTextCode.setText(lesson.initialCode)
            if (lesson.taskDescription.isNotBlank()) {
                binding.tvTaskTitle.visibility = View.VISIBLE
                binding.tvTask.visibility = View.VISIBLE
                binding.tvTask.text =
                    Html.fromHtml(lesson.taskDescription, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.tvTaskTitle.visibility = View.GONE
                binding.tvTask.visibility = View.GONE
            }
        }

        binding.btnRun.setOnClickListener {
            val code = binding.editTextCode.text.toString()
            val input = binding.editTextInput.text.toString()
            val output = codeEditorHelper.executePythonCode(code, input)
            binding.tvOutput.text = output
            val repository = (context?.applicationContext as MyApp).repository
            CoroutineScope(Dispatchers.IO).launch {
                repository.recordAction("run_code")
            }
        }

        binding.btnTest.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragment_container, TestFragment.Companion.newInstance(moduleId, lessonId))
                addToBackStack(null)
            }
        }

        val parentFragment = requireParentFragment()
        if (parentFragment is LessonsPagerFragment) {
            val viewPager = parentFragment.requireView().findViewById<ViewPager2>(R.id.view_pager)

            // Инициализация GestureDetector
            gestureDetector = GestureDetector(
                requireContext(),
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onScroll(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        distanceX: Float,
                        distanceY: Float
                    ): Boolean {
                        // Если горизонтальное перемещение превышает вертикальное, считаем, что это горизонтальный скролл
                        if (Math.abs(distanceX) > Math.abs(distanceY)) {
                            isHorizontalScroll = true
                            viewPager.requestDisallowInterceptTouchEvent(true)
                        } else {
                            isHorizontalScroll = false
                        }
                        return false
                    }
                })

            binding.editTextCode.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Начало касания — сбрасываем флаг и блокируем ViewPager
                        isHorizontalScroll = false
                        viewPager.requestDisallowInterceptTouchEvent(true)
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Жест завершён — разрешаем ViewPager снова
                        viewPager.requestDisallowInterceptTouchEvent(false)
                    }
                }
                // Важно: возвращаем false, чтобы CodeView сам обработал событие
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun undo() = codeEditorHelper.undo()
    override fun redo() = codeEditorHelper.redo()
    override fun commentSelected() = codeEditorHelper.commentSelected()
    override fun unCommentSelected() = codeEditorHelper.unCommentSelected()
    override fun findAndReplace() =
        codeEditorHelper.showFindAndReplaceDialog(activity as AppCompatActivity)

    override fun clearText() = codeEditorHelper.clearText()
    override fun toggleRelativeLineNumber() = codeEditorHelper.toggleRelativeLineNumber()
    override fun changeTheme(themeId: Int) = codeEditorHelper.changeTheme(themeId)

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