package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val time: String,
    val type: String,
    val repeatDays: String,
    val isActive: Boolean,
    val color: Int?
)
