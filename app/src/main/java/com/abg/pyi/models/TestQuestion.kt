package com.abg.pyi.models

data class TestQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String = ""
)

data class TestResult(
    val correctCount: Int,
    val total: Int,
    val passed: Boolean
)