package com.example.cuidado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val conditions: String,
    val frequentMedications: String
)