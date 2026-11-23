package com.example.cuidado.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicionDao {

    @Query("SELECT * FROM mediciones ORDER BY fecha DESC, hora DESC")
    fun observeMediciones(): Flow<List<MedicionEntity>>

    @Query("SELECT * FROM mediciones WHERE tipo = :tipo ORDER BY fecha DESC, hora DESC")
    fun observeMedicionesByType(tipo: String): Flow<List<MedicionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicion(medicion: MedicionEntity): Long

    @Query("SELECT * FROM rangos_medicion")
    fun getAllRangos(): List<RangoMedicionEntity>

    @Query("SELECT * FROM rangos_medicion WHERE tipo = :tipo LIMIT 1")
    fun getRangoByType(tipo: String): RangoMedicionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRango(rango: RangoMedicionEntity)
}
