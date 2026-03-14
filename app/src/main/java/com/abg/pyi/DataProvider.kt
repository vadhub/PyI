package com.abg.pyi

import android.content.Context
import java.io.BufferedReader

object DataProvider {

    fun getModules(context: Context): List<Module> {
        val module1Lessons = listOf(
            createLesson(context, 1, 1, "1.1. Первая программа"),
            createLesson(context, 1, 2, "1.2. Переменные и типы данных"),
            createLesson(context, 1, 3, "1.3. Ввод данных")
        )
        return listOf(
            Module(1, "Модуль 1. Знакомство с Python", module1Lessons)
        )
    }

    private fun createLesson(context: Context, moduleId: Int, lessonId: Int, title: String): Lesson {
        val theory = readAssetFile(context, "lessons/module$moduleId/lesson$lessonId/theory.txt")
        val code = readAssetFile(context, "lessons/module$moduleId/lesson$lessonId/code.txt")
        val task = readAssetFile(context, "lessons/module$moduleId/lesson$lessonId/task.txt")
        return Lesson(lessonId, title, theory, code, task)
    }

    private fun readAssetFile(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
        } catch (e: Exception) {
            "Не удалось загрузить содержимое файла $fileName"
        }
    }
}