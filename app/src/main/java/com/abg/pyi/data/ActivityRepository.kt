package com.abg.pyi.data

import com.abg.pyi.data.UserActivity
import com.abg.pyi.data.UserActivityDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ActivityRepository(private val dao: UserActivityDao) {

    suspend fun recordAction(actionType: String) {
        val today = LocalDate.now()
        val existing = dao.getActivityByDateAndType(today, actionType)
        if (existing != null) {
            val updated = existing.copy(count = existing.count + 1)
            dao.update(updated)
        } else {
            dao.insert(UserActivity(date = today, actionType = actionType, count = 1))
        }
    }

    fun getActivitySummaryForLastYear(): Flow<Map<LocalDate, Int>> {
        val startDate = LocalDate.now().minusDays(364)
        return dao.getActivitySummaryFlow(startDate).map { list ->
            list.associate { it.date to it.total }
        }
    }
}