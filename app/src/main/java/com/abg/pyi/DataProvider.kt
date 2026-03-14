package com.abg.pyi

object DataProvider {
    fun getModules(): List<Module> {
        val lessonsModule1 = listOf(
            Lesson(
                id = 1,
                title = "Первая программа",
                theory = """
                    Python — это популярный язык программирования.
                    Он используется для веб-разработки, анализа данных, искусственного интеллекта и многого другого.
                    
                    Первая программа на Python:
                    ```python
                    print("Привет, мир!")
Функция print() выводит текст на экран.
""".trimIndent(),
                initialCode = "print(\"Привет, мир!\")",
                taskDescription = "Напиши программу, которая выводит «Привет, мир!»."
            ),
        )

        val module1 = Module(
            id = 1,
            title = "Модуль 1. Знакомство с Python",
            lessons = lessonsModule1
        )

        return listOf(module1)
    }
}
