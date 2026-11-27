package com.example.cuidado

import android.app.Application
import com.example.cuidado.data.local.AppDatabase
import com.example.cuidado.data.repository.ActivityRepository
import com.example.cuidado.data.repository.CitaRepository
import com.example.cuidado.data.repository.MedicionRepository
import com.example.cuidado.data.repository.ProfileRepository
import com.example.cuidado.servicios.NotificacionScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CuidadoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        val database = AppDatabase.getDatabase(this)
        ActivityRepository.initialize(
            dao = database.activityDao(),
            logDao = database.activityLogDao()
        )
        ProfileRepository.initialize(this, database.profileDao())
        MedicionRepository.initialize(database.medicionDao())
        CitaRepository.initialize(database.citaDao())

        // Sincronizar recordatorios diarios de actividades activas (RF-008)
        val notificador = NotificacionScheduler(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val activities = database.activityDao().getAllActivities()
                activities.filter { it.isActive }.forEach { activity ->
                    notificador.Recordatorio_ProgramarActividadDiaria(activity.id, activity.name, activity.time)
                }
            } catch (_: Exception) {}
        }
    }
}
