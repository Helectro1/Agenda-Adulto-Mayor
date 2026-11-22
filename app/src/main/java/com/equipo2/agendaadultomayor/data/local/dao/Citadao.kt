package com.equipo2.agendaadultomayor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.equipo2.agendaadultomayor.data.entity.Cita

@Dao
interface CitaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCita(objCita: Cita): Long

    // Consulta todas las citas entre el inicio y fin de un día específico
    @Query("SELECT * FROM CITA WHERE dtFechaHora >= :dtInicioDia AND dtFechaHora <= :dtFinDia ORDER BY dtFechaHora ASC")
    suspend fun obtenerPorFecha(dtInicioDia: Long, dtFinDia: Long): List<Cita>

    // Consulta todas las citas entre el inicio y fin de un mes específico
    @Query("SELECT * FROM CITA WHERE dtFechaHora >= :dtInicioMes AND dtFechaHora <= :dtFinMes ORDER BY dtFechaHora ASC")
    suspend fun obtenerPorMes(dtInicioMes: Long, dtFinMes: Long): List<Cita>

    @Query("DELETE FROM CITA WHERE lIdCita = :lIdCita")
    suspend fun eliminarCita(lIdCita: Long): Int
}