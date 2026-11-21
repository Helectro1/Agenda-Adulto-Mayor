package com.example.cuidado.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activities ORDER BY time ASC")
    fun observeActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities")
    fun getAllActivities(): List<ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivity(activity: ActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivities(activities: List<ActivityEntity>)

    @Update
    fun updateActivity(activity: ActivityEntity): Int

    @Delete
    fun deleteActivity(activity: ActivityEntity): Int
}
