package com.example.cuidado.servicios

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cuidado.interfaces.servicios.OrigenRecordatorio
import com.example.cuidado.interfaces.servicios.iNotificador
import com.example.cuidado.interfaces.servicios.iRecordatorio
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificacionScheduler(
    private val context: Context
) : iNotificador {

    override fun Recordatorio_Programar(objRecordatorio: iRecordatorio) {
        val ahora = System.currentTimeMillis()
        val retrasoMs = objRecordatorio.dtDisparo - ahora

        if (retrasoMs <= 0L) return

        val datos = workDataOf(
            "lIdOrigen" to objRecordatorio.lIdOrigen,
            "eOrigen" to objRecordatorio.eOrigen.name,
            "strTitulo" to objRecordatorio.strTitulo,
            "dtDisparo" to objRecordatorio.dtDisparo
        )

        val workRequest = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(retrasoMs, TimeUnit.MILLISECONDS)
            .setInputData(datos)
            .build()

        val nombreUnico = "recordatorio_${objRecordatorio.eOrigen.name}_${objRecordatorio.lIdOrigen}"

        WorkManager.getInstance(context).enqueueUniqueWork(
            nombreUnico,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun Recordatorio_Cancelar(lIdOrigen: Long, eOrigen: OrigenRecordatorio) {
        val nombreUnico = "recordatorio_${eOrigen.name}_$lIdOrigen"
        WorkManager.getInstance(context).cancelUniqueWork(nombreUnico)
    }

    override fun Recordatorio_ProgramarActividadDiaria(idActividad: String, titulo: String, horaStr: String) {
        val partes = horaStr.split(":")
        if (partes.size != 2) return
        val hora = partes[0].toIntOrNull() ?: return
        val minuto = partes[1].toIntOrNull() ?: return

        val calendarActual = Calendar.getInstance()
        val calendarObjetivo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendarObjetivo.timeInMillis <= calendarActual.timeInMillis) {
            calendarObjetivo.add(Calendar.DAY_OF_YEAR, 1)
        }

        val retrasoMs = calendarObjetivo.timeInMillis - calendarActual.timeInMillis

        val datos = workDataOf(
            "lIdOrigen" to idActividad.hashCode().toLong(),
            "eOrigen" to OrigenRecordatorio.ACTIVIDAD.name,
            "strTitulo" to "Recordatorio: $titulo",
            "dtDisparo" to calendarObjetivo.timeInMillis
        )

        val workRequest = PeriodicWorkRequestBuilder<RecordatorioWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(retrasoMs, TimeUnit.MILLISECONDS)
            .setInputData(datos)
            .build()

        val nombreUnico = "recordatorio_actividad_$idActividad"

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            nombreUnico,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    override fun Recordatorio_CancelarActividadDiaria(idActividad: String) {
        val nombreUnico = "recordatorio_actividad_$idActividad"
        WorkManager.getInstance(context).cancelUniqueWork(nombreUnico)
    }
}
