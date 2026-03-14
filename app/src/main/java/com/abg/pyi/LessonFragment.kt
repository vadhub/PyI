
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abg.pyi.DataProvider
import com.abg.pyi.databinding.FragmentLessonBinding
import com.abg.pyi.code_editor.CodeEditorHelper
import com.abg.pyi.code_editor.ICodeEditorActions

class LessonFragment : Fragment(), ICodeEditorActions {

    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeEditorHelper = CodeEditorHelper( binding.editTextCode, requireContext())
        codeEditorHelper.setup()

        val module = DataProvider.getModules(requireContext()).find { it.id == moduleId }
        val lesson = module?.lessons?.find { it.id == lessonId }

        if (lesson != null) {
            binding.tvTheory.text = lesson.theory
            binding.editTextCode.setText(lesson.initialCode)

            if (lesson.taskDescription.isNotBlank()) {
                binding.tvTaskTitle.visibility = View.VISIBLE
                binding.tvTask.visibility = View.VISIBLE
                binding.tvTask.text = lesson.taskDescription
            } else {
                binding.tvTaskTitle.visibility = View.GONE
                binding.tvTask.visibility = View.GONE
            }
        }

        binding.btnRun.setOnClickListener {
            val code = binding.editTextCode.text.toString()
            val output = codeEditorHelper.executePythonCode(code)
            binding.tvOutput.text = output
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
    override fun findAndReplace() = codeEditorHelper.showFindAndReplaceDialog(activity as AppCompatActivity)
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