package com.abg.pyi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface UserActivityDao {
    @Insert
    suspend fun insert(activity: UserActivity)

    @Update
    suspend fun update(activity: UserActivity)

    @Query("SELECT * FROM user_activity WHERE date = :date AND actionType = :actionType")
    suspend fun getActivityByDateAndType(date: LocalDate, actionType: String): UserActivity?

    @Query("SELECT date, SUM(count) as total FROM user_activity WHERE date >= :startDate GROUP BY date ORDER BY date ASC")
    fun getActivitySummaryFlow(startDate: LocalDate): Flow<List<DateCount>>

    data class DateCount(
        val date: LocalDate,
        val total: Int
    )
}