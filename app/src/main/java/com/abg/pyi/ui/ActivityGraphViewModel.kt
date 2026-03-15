package com.abg.pyi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abg.pyi.data.ActivityRepository
import com.abg.pyi.models.CalendarDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActivityGraphViewModel(private val repository: ActivityRepository) : ViewModel() {

    private val _calendarDays = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarDays: StateFlow<List<CalendarDay>> = _calendarDays

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getActivitySummaryForLastYear().collect { summaryMap ->
                val today = LocalDate.now()
                val startDate = today.minusDays(364)
                val daysList = mutableListOf<CalendarDay>()
                var current = startDate
                while (current <= today) {
                    val count = summaryMap[current] ?: 0
                    val level = when {
                        count == 0 -> 0
                        count < 3 -> 1
                        count < 6 -> 2
                        count < 10 -> 3
                        else -> 4
                    }
                    daysList.add(CalendarDay(current, count, level))
                    current = current.plusDays(1)
                }
                _calendarDays.value = daysList
            }
        }
    }
}

class ActivityGraphViewModelFactory(private val repository: ActivityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityGraphViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityGraphViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}