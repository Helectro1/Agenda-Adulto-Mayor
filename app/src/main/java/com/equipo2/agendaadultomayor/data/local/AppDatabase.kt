package com.equipo2.agendaadultomayor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.equipo2.agendaadultomayor.data.entity.Cita
import com.equipo2.agendaadultomayor.data.local.dao.CitaDao

@Database(
    entities = [Cita::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    // Método exigido por el componente Persistencia (RF-018)
    suspend fun BaseDatos_GuardarConfirmado(accionGuardar: suspend () -> Boolean): Boolean {
        return try {
            withTransaction {
                accionGuardar()
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun BaseDatos_ObtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agenda_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}