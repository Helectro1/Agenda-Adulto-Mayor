package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_logs",
    indices = [Index(value = ["activityId", "date"], unique = true)]
)
data class ActivityLogEntity(
    @PrimaryKey
    val id: String,
    val activityId: String,
    val date: String,
    val status: String,
    val executionTime: String?
)
