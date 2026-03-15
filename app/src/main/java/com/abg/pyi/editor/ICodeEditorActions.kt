package com.abg.pyi.editor

interface ICodeEditorActions {
    fun undo()
    fun redo()
    fun commentSelected()
    fun unCommentSelected()
    fun findAndReplace()
    fun clearText()
    fun toggleRelativeLineNumber()
    fun changeTheme(themeId: Int)
}