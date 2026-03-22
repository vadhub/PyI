package com.abg.pyi.data

import android.content.Context
import com.abg.pyi.models.Lesson
import com.abg.pyi.models.Module
import com.abg.pyi.models.TestQuestion
import org.json.JSONObject
import java.io.BufferedReader

object DataProvider {

    fun getModules(context: Context): List<Module> {

        val module0Lessons = listOf(
            createLesson(context, 0, 1, "0.1. Зачем нужна локальная среда?"),
            createLesson(context, 0, 2, "0.2. Установка Python"),
            createLesson(context, 0, 3, "0.3. Знакомство с терминалом"),
            createLesson(context, 0, 4, "0.4. Редактор кода (VS Code)"),
            createLesson(context, 0, 5, "0.5. Виртуальные окружения и pip"),
        )

        val module1Lessons = listOf(
            createLesson(context, 1, 1, "1.1. Первая программа"),
            createLesson(context, 1, 2, "1.2. Переменные и типы данных"),
            createLesson(context, 1, 3, "1.3. Ввод данных")
        )

        val module2Lessons = listOf(
            createLesson(context, 2, 1, "2.1. Логические выражения"),
            createLesson(context, 2, 2, "2.2. Конструкция if-else"),
            createLesson(context, 2, 3, "2.3. Сложные условия"),
            createLesson(context, 2, 4, "2.4. Проект: Угадай число")
        )

        val module3Lessons = listOf(
            createLesson(context, 3, 1, "3.1. Цикл while"),
            createLesson(context, 3, 2, "3.2. Цикл for"),
            createLesson(context, 3, 3, "3.3. Управление циклом"),
            createLesson(context, 3, 4, "3.4. Проект: Угадай число (с циклом)")
        )

        val module4Lessons = listOf(
            createLesson(context, 4, 1, "4.1. Создание списков"),
            createLesson(context, 4, 2, "4.2. Методы списков"),
            createLesson(context, 4, 3, "4.3. Перебор списков"),
            createLesson(context, 4, 4, "4.4. Генераторы списков")
        )

        val module5Lessons = listOf(
            createLesson(context, 5, 1, "5.1. Определение функции"),
            createLesson(context, 5, 2, "5.2. Аргументы и возврат значений"),
            createLesson(context, 5, 3, "5.3. Области видимости"),
            createLesson(context, 5, 4, "5.4. Проект: Калькулятор")
        )

        val module6Lessons = listOf(
            createLesson(context, 6, 1, "6.1. Методы строк"),
            createLesson(context, 6, 2, "6.2. Форматирование строк"),
            createLesson(context, 6, 3, "6.3. Срезы строк"),
            createLesson(context, 6, 4, "6.4. Проект: Шифр Цезаря")
        )

        val module7Lessons = listOf(
            createLesson(context, 7, 1, "7.1. Словари"),
            createLesson(context, 7, 2, "7.2. Методы словарей"),
            createLesson(context, 7, 3, "7.3. Множества"),
            createLesson(context, 7, 4, "7.4. Проект: Телефонный справочник")
        )

        val module8Lessons = listOf(
            createLesson(context, 8, 1, "8.1. Чтение из файла"),
            createLesson(context, 8, 2, "8.2. Запись в файл"),
            createLesson(context, 8, 3, "8.3. Контекстный менеджер with"),
            createLesson(context, 8, 4, "8.4. Проект: Записная книжка")
        )

        val module9Lessons = listOf(
            createLesson(context, 9, 1, "9.1. Классы и объекты"),
            createLesson(context, 9, 2, "9.2. Конструктор __init__"),
            createLesson(context, 9, 3, "9.3. Наследование"),
            createLesson(context, 9, 4, "9.4. Проект: Банковский счёт")
        )
        val module10Lessons = listOf(
            createLesson(context, 10, 1, "10.1. Проектирование игры"),
            createLesson(context, 10, 2, "10.2. Отображение поля"),
            createLesson(context, 10, 3, "10.3. Ход игрока"),
            createLesson(context, 10, 4, "10.4. Проверка победы"),
            createLesson(context, 10, 5, "10.5. Ход компьютера (случайный)"),
            createLesson(context, 10, 6, "10.6. Игровой цикл"),
            createLesson(context, 10, 7, "10.7. Улучшаем AI"),
            createLesson(context, 10, 8, "10.8. Финальный билд и тестирование")
        )

        return listOf(
            Module(0, "Модуль 0. Настройка среды разработки", module0Lessons),
            Module(1, "Модуль 1. Знакомство с Python", module1Lessons),
            Module(2, "Модуль 2. Условия и логика", module2Lessons),
            Module(3, "Модуль 3. Циклы", module3Lessons),
            Module(4, "Модуль 4. Списки", module4Lessons),
            Module(5, "Модуль 5. Функции", module5Lessons),
            Module(6, "Модуль 6. Строки и работа с текстом", module6Lessons),
            Module(7, "Модуль 7. Словари и множества", module7Lessons),
            Module(8, "Модуль 8. Работа с файлами", module8Lessons),
            Module(9, "Модуль 9. ООП", module9Lessons),
            Module(10, "Модуль 10. Итоговый проект: Крестики-нолики", module10Lessons)
        )
    }

    private fun createLesson(
        context: Context,
        moduleId: Int,
        lessonId: Int,
        title: String
    ): Lesson {
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

    fun getTestQuestions(context: Context, moduleId: Int, lessonId: Int): List<TestQuestion> {
        val fileName = "lessons/module$moduleId/lesson$lessonId/test.json"
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)
            val questionsArray = json.getJSONArray("questions")
            val questions = mutableListOf<TestQuestion>()
            for (i in 0 until questionsArray.length()) {
                val qObj = questionsArray.getJSONObject(i)
                val question = qObj.getString("question")
                val optionsArray = qObj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }
                val correct = qObj.getInt("correct")
                val explanation = qObj.optString("explanation", "")
                questions.add(TestQuestion(question, options, correct, explanation))
            }
            questions
        } catch (e: Exception) {
            emptyList()
        }
    }
}