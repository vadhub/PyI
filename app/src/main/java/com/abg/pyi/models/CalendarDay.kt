package com.abg.pyi.models

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val count: Int,
    val colorLevel: Int
)