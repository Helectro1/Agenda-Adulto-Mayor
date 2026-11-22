package com.equipo2.agendaadultomayor.servicios

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.equipo2.agendaadultomayor.interfaces.servicios.OrigenRecordatorio
import com.equipo2.agendaadultomayor.interfaces.servicios.iNotificador
import com.equipo2.agendaadultomayor.interfaces.servicios.iRecordatorio
import java.util.concurrent.TimeUnit

class NotificacionScheduler(
    private val context: Context
) : iNotificador {

    override fun Recordatorio_Programar(objRecordatorio: iRecordatorio) {
        val ahora = System.currentTimeMillis()
        val retrasoMs = objRecordatorio.dtDisparo - ahora

        // Solo programamos si la fecha/hora aún no ha pasado
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

        // Nombre único por tipo e ID para poder cancelarlo o reemplazarlo fácilmente
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
}