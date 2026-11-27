package com.example.cuidado.servicios

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.cuidado.data.local.AppDatabase
import com.example.cuidado.interfaces.servicios.OrigenRecordatorio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = NotificacionScheduler(context)
            val database = AppDatabase.getDatabase(context)
            val activityDao = database.activityDao()
            val citaDao = database.citaDao()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val activities = activityDao.getAllActivities()
                    for (activity in activities) {
                        if (activity.isActive) {
                            if (activity.time.startsWith("EVERY_")) {
                                val timePart = activity.time.removePrefix("EVERY_")
                                val parts = timePart.split(":")
                                if (parts.size == 2) {
                                    val h = parts[0].toLongOrNull() ?: 0L
                                    val m = parts[1].toLongOrNull() ?: 0L
                                    val totalMins = h * 60 + m
                                    if (totalMins > 0) {
                                        scheduler.programarAlarmaIntervalo(activity.id, activity.name, totalMins)
                                    }
                                }
                            } else if (activity.time.isNotBlank()) {
                                scheduler.Recordatorio_ProgramarActividadDiaria(activity.id, activity.name, activity.time)
                            }
                        }
                    }

                    val ahora = System.currentTimeMillis()
                    val citas = citaDao.obtenerCitasEnRango(ahora, ahora + 30L * 24 * 60 * 60 * 1000)
                    for (cita in citas) {
                        val recordatorio = com.example.cuidado.interfaces.servicios.iRecordatorio(
                            lIdOrigen = cita.lIdCita,
                            eOrigen = OrigenRecordatorio.CITA,
                            strTitulo = "Cita con ${cita.strEspecialista} en ${cita.strUbicacion}",
                            dtDisparo = cita.dtFechaHora
                        )
                        scheduler.Recordatorio_Programar(recordatorio)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
