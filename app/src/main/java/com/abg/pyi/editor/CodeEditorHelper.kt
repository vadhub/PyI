package com.abg.pyi.editor

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.abg.pyi.R
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeview.CodeView
import com.chaquo.python.Python
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.regex.Pattern

class CodeEditorHelper(
    private val codeView: CodeView,
    private val context: Context,
    private val languageNameTextView: TextView? = null,
    private val sourcePositionTextView: TextView? = null
) {
    private lateinit var languageManager: LanguageManager
    private lateinit var commentManager: CommentManager
    private lateinit var undoRedoManager: UndoRedoManager
    private var currentTheme: LanguageManager.ThemeName = LanguageManager.ThemeName.MONOKAI
    private val useModernAutoCompleteAdapter = true

    fun setup(language: String = "Python") {
        configureCodeView()
        configurePlugins()
        setLanguageName(language)
    }

    private fun configureCodeView() {
        // Set font
        val jetBrainsMono = ResourcesCompat.getFont(context, R.font.jetbrains_mono_medium)
        codeView.setTypeface(jetBrainsMono)

        // Line numbers
        codeView.setEnableLineNumber(true)
        codeView.setLineNumberTextColor(Color.GRAY)
        codeView.setLineNumberTextSize(18f)

        codeView.setHorizontallyScrolling(true)           // включает горизонтальную прокрутку текста
        codeView.isHorizontalScrollBarEnabled = true      // показывает полосу прокрутки
        codeView.isVerticalScrollBarEnabled = true

        // Highlight current line
        codeView.setEnableHighlightCurrentLine(true)
        codeView.setHighlightCurrentLineColor(Color.GRAY)

        // Auto indentation
        codeView.setTabLength(4)
        codeView.setEnableAutoIndentation(true)

        // Language manager and theme
        languageManager = LanguageManager(context, codeView)
        languageManager.applyTheme(currentTheme)

        // Pair complete
        val pairCompleteMap: MutableMap<Char?, Char?> = HashMap()
        pairCompleteMap['{'] = '}'
        pairCompleteMap['['] = ']'
        pairCompleteMap['('] = ')'
        pairCompleteMap['"'] = '"'
        pairCompleteMap['\''] = '\''
        codeView.setPairCompleteMap(pairCompleteMap)
        codeView.enablePairComplete(true)
        codeView.enablePairCompleteCenterCursor(true)

        // Auto complete and indentation
        configureAutoComplete()
        configureAutoIndentation()
    }

    private fun configureAutoComplete() {
        if (useModernAutoCompleteAdapter) {
            val codeList: List<Code?> = languageManager.languageCodeList
            val adapter = CustomCodeViewAdapter(context, codeList)
            codeView.setAdapter(adapter)
        } else {
            val languageKeywords: Array<String?> = languageManager.languageKeywords
            val layoutId = R.layout.list_item_suggestion
            val viewId = R.id.suggestItemTextView
            val adapter = ArrayAdapter(context, layoutId, viewId, languageKeywords)
            codeView.setAdapter(adapter)
        }
    }

    private fun configureAutoIndentation() {
        codeView.setIndentationStarts(languageManager.languageIndentationStarts)
        codeView.setIndentationEnds(languageManager.languageIndentationEnds)
    }

    private fun configurePlugins() {
        commentManager = CommentManager(codeView)
        commentManager.setCommentStart(languageManager.commentStart)
        commentManager.setCommendEnd(languageManager.commentEnd)

        undoRedoManager = UndoRedoManager(codeView)
        undoRedoManager.connect()

        configureSourcePositionListener()
    }

    private fun configureSourcePositionListener() {
        val listener = SourcePositionListener(codeView)
        listener.setOnPositionChanged { line, column ->
            sourcePositionTextView?.text = context.getString(R.string.source_position, line, column)
        }
    }

    private fun setLanguageName(name: String) {
        languageNameTextView?.text = name
    }

    fun setInitialCode(code: String) {
        codeView.setText(code)
    }

    fun getCode(): String = codeView.text.toString()

    fun executePythonCode(code: String, input: String): String {
        return try {
            val py = Python.getInstance()
            val executor = py.getModule("executor")
            executor.callAttr("execute", code, input).toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    // --- Actions for menu ---
    fun undo() = undoRedoManager.undo()
    fun redo() = undoRedoManager.redo()
    fun commentSelected() = commentManager.commentSelected()
    fun unCommentSelected() = commentManager.unCommentSelected()
    fun clearText() = codeView.setText("")

    fun toggleRelativeLineNumber() {
        val enabled = !codeView.isLineRelativeNumberEnabled
        codeView.setEnableRelativeLineNumber(enabled)
    }

    fun changeTheme(themeId: Int) {
        val newTheme = when (themeId) {
            R.id.theme_monokia -> LanguageManager.ThemeName.MONOKAI
            R.id.theme_noctics -> LanguageManager.ThemeName.NOCTIS_WHITE
            R.id.theme_five_color -> LanguageManager.ThemeName.FIVE_COLOR
            R.id.theme_orange_box -> LanguageManager.ThemeName.ORANGE_BOX
            else -> return
        }
        if (newTheme != currentTheme) {
            currentTheme = newTheme
            languageManager.applyTheme(currentTheme)
        }
    }

    fun showFindAndReplaceDialog(activity: AppCompatActivity) {
        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.window?.setDimAmount(0f)

        val searchEdit = dialog.findViewById<EditText>(R.id.search_edit)
        val replacementEdit = dialog.findViewById<EditText>(R.id.replacement_edit)
        val findPrevAction = dialog.findViewById<ImageButton>(R.id.find_prev_action)
        val findNextAction = dialog.findViewById<ImageButton>(R.id.find_next_action)
        val replacementAction = dialog.findViewById<ImageButton>(R.id.replace_action)

        searchEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val text = editable.toString().trim()
                if (text.isEmpty()) codeView.clearMatches()
                else codeView.findMatches(Pattern.quote(text))
            }
        })

        findPrevAction?.setOnClickListener { codeView.findPrevMatch() }
        findNextAction?.setOnClickListener { codeView.findNextMatch() }
        replacementAction?.setOnClickListener {
            val regex = searchEdit?.text.toString()
            val replacement = replacementEdit?.text.toString()
            codeView.replaceAllMatches(regex, replacement)
        }

        dialog.setOnDismissListener { codeView.clearMatches() }
        dialog.show()
    }
}