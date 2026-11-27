package com.example.cuidado.servicios

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.cuidado.interfaces.servicios.OrigenRecordatorio
import com.example.cuidado.interfaces.servicios.iNotificador
import com.example.cuidado.interfaces.servicios.iRecordatorio
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificacionScheduler(
    private val context: Context
) : iNotificador {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun Recordatorio_Programar(objRecordatorio: iRecordatorio) {
        val ahora = System.currentTimeMillis()
        if (objRecordatorio.dtDisparo <= ahora) return

        val intent = Intent(context, RecordatorioAlarmReceiver::class.java).apply {
            putExtra("lIdOrigen", objRecordatorio.lIdOrigen)
            putExtra("eOrigen", objRecordatorio.eOrigen.name)
            putExtra("strTitulo", objRecordatorio.strTitulo)
            putExtra("dtDisparo", objRecordatorio.dtDisparo)
        }

        val requestCode = "${objRecordatorio.eOrigen.name}_${objRecordatorio.lIdOrigen}".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        programarAlarmaExacta(objRecordatorio.dtDisparo, pendingIntent)
    }

    override fun Recordatorio_Cancelar(lIdOrigen: Long, eOrigen: OrigenRecordatorio) {
        val intent = Intent(context, RecordatorioAlarmReceiver::class.java)
        val requestCode = "${eOrigen.name}_$lIdOrigen".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
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

        val disparoTime = calendarObjetivo.timeInMillis

        val intent = Intent(context, RecordatorioAlarmReceiver::class.java).apply {
            putExtra("lIdOrigen", idActividad.hashCode().toLong())
            putExtra("eOrigen", OrigenRecordatorio.ACTIVIDAD.name)
            putExtra("strTitulo", "Recordatorio: $titulo")
            putExtra("dtDisparo", disparoTime)
            putExtra("idActividad", idActividad)
            putExtra("horaStr", horaStr)
        }

        val requestCode = "recordatorio_actividad_$idActividad".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        programarAlarmaExacta(disparoTime, pendingIntent)
    }

    override fun Recordatorio_CancelarActividadDiaria(idActividad: String) {
        val intent = Intent(context, RecordatorioAlarmReceiver::class.java)
        val requestCode = "recordatorio_actividad_$idActividad".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun programarAlarmaIntervalo(idActividad: String, titulo: String, intervaloMinutos: Long) {
        val ahora = System.currentTimeMillis()
        val disparoTime = ahora + TimeUnit.MINUTES.toMillis(intervaloMinutos)

        val intent = Intent(context, RecordatorioAlarmReceiver::class.java).apply {
            putExtra("lIdOrigen", idActividad.hashCode().toLong())
            putExtra("eOrigen", OrigenRecordatorio.ACTIVIDAD.name)
            putExtra("strTitulo", "Recordatorio: $titulo")
            putExtra("dtDisparo", disparoTime)
            putExtra("idActividad", idActividad)
            putExtra("intervaloMinutos", intervaloMinutos)
        }

        val requestCode = "recordatorio_actividad_$idActividad".hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        programarAlarmaExacta(disparoTime, pendingIntent)
    }

    private fun programarAlarmaExacta(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
