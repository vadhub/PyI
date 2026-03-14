package com.abg.pyi

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeview.CodeView
import com.chaquo.python.Python
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var codeView: CodeView
    private lateinit var languageManager: LanguageManager
    private lateinit var commentManager: CommentManager
    private lateinit var undoRedoManager: UndoRedoManager

    private lateinit var languageNameText: TextView
    private lateinit var sourcePositionText: TextView

    private var currentTheme: LanguageManager.ThemeName = LanguageManager.ThemeName.MONOKAI

    private val useModernAutoCompleteAdapter = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configCodeView()
        configCodeViewPlugins()

//        codeView.setEnableLineNumber(true)
//        codeView.setEnableHighlightCurrentLine(true)
//        codeView.setHighlightCurrentLineColor(Color.GRAY)

// Настройка редактора (подсветка Python, тема)

        val pythonCode = """
    import os

    def greet(name):
        # A simple comment
        message = f"Hello, {name}!"
        return message

    if __name__ == "__main__":
        print(greet("World"))
        count = 10
""".trimIndent()
        codeView.setText(pythonCode)

        val buttonRun = findViewById<Button>(R.id.buttonRun)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        buttonRun.setOnClickListener {
            val code = codeView.text.toString()
            if (code.isNotBlank()) {
                val result = executePythonCode(code)
                textViewResult.text = result
            }
        }
    }

    private fun executePythonCode(code: String): String {
        return try {
            val py = Python.getInstance()
            val executor = py.getModule("executor")
            executor.callAttr("execute", code).toString()
        } catch (e: Exception) {
            "error: ${e.message}"
        }
    }

    private fun configCodeView() {
        codeView = findViewById<CodeView>(R.id.editTextCode)
        // Change default font to JetBrains Mono font
        val jetBrainsMono = ResourcesCompat.getFont(this, R.font.jetbrains_mono_medium)
        codeView.setTypeface(jetBrainsMono)

        // Setup Line number feature
        codeView.setEnableLineNumber(true)
        codeView.setLineNumberTextColor(Color.GRAY)
        codeView.setLineNumberTextSize(25f)

        // Setup highlighting current line
        codeView.setEnableHighlightCurrentLine(true)
        codeView.setHighlightCurrentLineColor(Color.GRAY)

        // Setup Auto indenting feature
        codeView.setTabLength(4)
        codeView.setEnableAutoIndentation(true)

        // Setup the language and theme with SyntaxManager helper class
        languageManager = LanguageManager(this, codeView)
        languageManager.applyTheme(currentTheme)

        // Setup auto pair complete
        val pairCompleteMap: MutableMap<Char?, Char?> = HashMap<Char?, Char?>()
        pairCompleteMap.put('{', '}')
        pairCompleteMap.put('[', ']')
        pairCompleteMap.put('(', ')')
        pairCompleteMap.put('<', '>')
        pairCompleteMap.put('"', '"')
        pairCompleteMap.put('\'', '\'')

        codeView.setPairCompleteMap(pairCompleteMap)
        codeView.enablePairComplete(true)
        codeView.enablePairCompleteCenterCursor(true)

        // Setup the auto complete and auto indenting for the current language
        configLanguageAutoComplete()
        configLanguageAutoIndentation()
    }

    private fun configLanguageAutoComplete() {
        if (useModernAutoCompleteAdapter) {
            // Load the code list (keywords and snippets) for the current language
            val codeList: List<Code?> = languageManager.languageCodeList

            // Use CodeViewAdapter or custom one
            val adapter: CustomCodeViewAdapter = CustomCodeViewAdapter(this, codeList)

            // Add the odeViewAdapter to the CodeView
            codeView.setAdapter(adapter)
        } else {
            val languageKeywords: Array<String?> =
                languageManager.languageKeywords

            // Custom list item xml layout
            val layoutId: Int = R.layout.list_item_suggestion

            // TextView id to put suggestion on it
            val viewId: Int = R.id.suggestItemTextView
            val adapter = ArrayAdapter<String?>(this, layoutId, viewId, languageKeywords)

            // Add the ArrayAdapter to the CodeView
            codeView.setAdapter(adapter)
        }
    }

    private fun configLanguageAutoIndentation() {
        codeView.setIndentationStarts(languageManager.languageIndentationStarts)
        codeView.setIndentationEnds(languageManager.languageIndentationEnds)
    }

    private fun configCodeViewPlugins() {
        commentManager = CommentManager(codeView)
        configCommentInfo()

        undoRedoManager = UndoRedoManager(codeView)
        undoRedoManager.connect()

        languageNameText = findViewById<View?>(R.id.language_name_txt) as TextView
        configLanguageName()

        sourcePositionText = findViewById<View?>(R.id.source_position_txt) as TextView
        sourcePositionText.setText(getString(R.string.source_position, 0, 0))
        configSourcePositionListener()
    }

    private fun configCommentInfo() {
        commentManager.setCommentStart(languageManager.commentStart)
        commentManager.setCommendEnd(languageManager.commentEnd)
    }

    private fun configLanguageName() {
        languageNameText.text = "Python"
    }

    private fun configSourcePositionListener() {
        val sourcePositionListener: SourcePositionListener = SourcePositionListener(codeView)
        sourcePositionListener.setOnPositionChanged({ line, column ->
            sourcePositionText.text = getString(R.string.source_position, line, column)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        val menuItemId = item.itemId
        val menuGroupId = item.groupId

        if (menuGroupId == R.id.group_themes) changeTheEditorTheme(menuItemId)
        else if (menuItemId == R.id.findMenu) launchEditorButtonSheet()
        else if (menuItemId == R.id.comment) commentManager.commentSelected()
        else if (menuItemId == R.id.un_comment) commentManager.unCommentSelected()
        else if (menuItemId == R.id.clearText) codeView.setText("")
        else if (menuItemId == R.id.toggle_relative_line_number) toggleRelativeLineNumber()
        else if (menuItemId == R.id.undo) undoRedoManager.undo()
        else if (menuItemId == R.id.redo) undoRedoManager.redo()

        return super.onOptionsItemSelected(item)
    }

    private fun changeTheEditorTheme(themeId: Int) {
        val oldTheme: LanguageManager.ThemeName? = currentTheme
        if (themeId == R.id.theme_monokia) currentTheme = LanguageManager.ThemeName.MONOKAI
        else if (themeId == R.id.theme_noctics) currentTheme =
            LanguageManager.ThemeName.NOCTIS_WHITE
        else if (themeId == R.id.theme_five_color) currentTheme =
            LanguageManager.ThemeName.FIVE_COLOR
        else if (themeId == R.id.theme_orange_box) currentTheme =
            LanguageManager.ThemeName.ORANGE_BOX

        if (currentTheme !== oldTheme) {
            languageManager.applyTheme(currentTheme)
        }
    }

    private fun toggleRelativeLineNumber() {
        var isRelativeLineNumberEnabled: Boolean = codeView.isLineRelativeNumberEnabled
        isRelativeLineNumberEnabled = !isRelativeLineNumberEnabled
        codeView.setEnableRelativeLineNumber(isRelativeLineNumberEnabled)
    }

    private fun launchEditorButtonSheet() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.window!!.setDimAmount(0f)

        val searchEdit = dialog.findViewById<EditText?>(R.id.search_edit)
        val replacementEdit = dialog.findViewById<EditText?>(R.id.replacement_edit)

        val findPrevAction = dialog.findViewById<ImageButton?>(R.id.find_prev_action)
        val findNextAction = dialog.findViewById<ImageButton?>(R.id.find_next_action)
        val replacementAction = dialog.findViewById<ImageButton?>(R.id.replace_action)

        searchEdit!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                val text = editable.toString().trim { it <= ' ' }
                if (text.isEmpty()) codeView.clearMatches()
                codeView.findMatches(Pattern.quote(text))
            }
        })

        findPrevAction!!.setOnClickListener { v: View? ->
            codeView.findPrevMatch()
        }

        findNextAction!!.setOnClickListener { v: View? ->
            codeView.findNextMatch()
        }

        replacementAction!!.setOnClickListener { v: View? ->
            val regex = searchEdit.getText().toString()
            val replacement = replacementEdit!!.getText().toString()
            codeView.replaceAllMatches(regex, replacement)
        }

        dialog.setOnDismissListener { c: DialogInterface? -> codeView.clearMatches() }
        dialog.show()
    }
}