package com.abg.pyi.models

data class Module(
    val id: Int,
    val title: String,
    val lessons: List<Lesson>
)