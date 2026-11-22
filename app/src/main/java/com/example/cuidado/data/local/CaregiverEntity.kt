package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caregiver")
data class CaregiverEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val phone: String
)