package com.example.cuidado.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 1")
    fun observeProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProfile(entity: ProfileEntity)

    @Query("SELECT * FROM caregiver WHERE id = 1")
    fun observeCaregiver(): Flow<CaregiverEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCaregiver(entity: CaregiverEntity)
}
