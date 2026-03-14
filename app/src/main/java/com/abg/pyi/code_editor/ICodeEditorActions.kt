package com.abg.pyi.code_editor

interface ICodeEditorActions {
    fun undo()
    fun redo()
    fun toggleComment()
    fun uncomment()
    fun clearText()
    fun findAndReplace()
    fun changeTheme(themeId: Int)
    fun toggleRelativeLineNumber()
}