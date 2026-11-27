package com.example.cuidado.servicios

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cuidado.interfaces.servicios.OrigenRecordatorio
import com.example.cuidado.interfaces.servicios.iRecordatorio

class RecordatorioAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val lIdOrigen = intent.getLongExtra("lIdOrigen", 0L)
        val strOrigen = intent.getStringExtra("eOrigen") ?: OrigenRecordatorio.CITA.name
        val strTitulo = intent.getStringExtra("strTitulo") ?: "Recordatorio"
        val dtDisparo = intent.getLongExtra("dtDisparo", System.currentTimeMillis())
        val intervaloMinutos = intent.getLongExtra("intervaloMinutos", 0L)
        val idActividadStr = intent.getStringExtra("idActividad") ?: ""
        val horaStr = intent.getStringExtra("horaStr") ?: ""

        val objRecordatorio = iRecordatorio(
            lIdOrigen = lIdOrigen,
            eOrigen = OrigenRecordatorio.valueOf(strOrigen),
            strTitulo = strTitulo,
            dtDisparo = dtDisparo
        )

        mostrarNotificacion(context, objRecordatorio)

        val scheduler = NotificacionScheduler(context)
        if (strOrigen == OrigenRecordatorio.ACTIVIDAD.name) {
            if (intervaloMinutos > 0) {
                scheduler.programarAlarmaIntervalo(idActividadStr, strTitulo.removePrefix("Recordatorio: "), intervaloMinutos)
            } else if (horaStr.isNotBlank()) {
                scheduler.Recordatorio_ProgramarActividadDiaria(idActividadStr, strTitulo.removePrefix("Recordatorio: "), horaStr)
            }
        }
    }

    private fun mostrarNotificacion(context: Context, objRecordatorio: iRecordatorio) {
        val channelId = "canal_recordatorios_agenda"

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
