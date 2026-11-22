package com.equipo2.agendaadultomayor.servicios

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.equipo2.agendaadultomayor.interfaces.servicios.OrigenRecordatorio
import com.equipo2.agendaadultomayor.interfaces.servicios.iRecordatorio

class RecordatorioWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val lIdOrigen = inputData.getLong("lIdOrigen", 0L)
        val strOrigen = inputData.getString("eOrigen") ?: OrigenRecordatorio.CITA.name
        val strTitulo = inputData.getString("strTitulo") ?: "Recordatorio de Agenda"
        val dtDisparo = inputData.getLong("dtDisparo", System.currentTimeMillis())

        val objRecordatorio = iRecordatorio(
            lIdOrigen = lIdOrigen,
            eOrigen = OrigenRecordatorio.valueOf(strOrigen),
            strTitulo = strTitulo,
            dtDisparo = dtDisparo
        )

        Recordatorio_MostrarNotificacion(objRecordatorio)
        return Result.success()
    }

    fun Recordatorio_MostrarNotificacion(objRecordatorio: iRecordatorio) {
        val channelId = "canal_recordatorios_agenda"

        // Crear el canal de notificación (obligatorio en Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Recordatorios de Citas y Actividades"
            val descripcion = "Notificaciones locales de Agenda Adulto Mayor"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(channelId, nombre, importancia).apply {
                description = descripcion
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }

        val subtitulo = if (objRecordatorio.eOrigen == OrigenRecordatorio.CITA) {
            "Cita médica programada"
        } else {
            "Actividad de cuidado programada"
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(subtitulo)
            .setContentText(objRecordatorio.strTitulo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Verificar permiso antes de lanzar la notificación
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            val notificationId = "${objRecordatorio.eOrigen.name}_${objRecordatorio.lIdOrigen}".hashCode()
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }
}