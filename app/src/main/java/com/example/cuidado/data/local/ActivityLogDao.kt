package com.example.cuidado.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    @Query("SELECT * FROM activity_logs WHERE date = :date")
    fun observeLogsForDate(date: String): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE activityId = :activityId")
    fun observeLogsForActivity(activityId: String): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE activityId = :activityId AND date = :date LIMIT 1")
    fun getLogForActivityAndDate(activityId: String, date: String): ActivityLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLog(log: ActivityLogEntity)

    @Update
    fun updateLog(log: ActivityLogEntity)

    @Delete
    fun deleteLog(log: ActivityLogEntity)
}
