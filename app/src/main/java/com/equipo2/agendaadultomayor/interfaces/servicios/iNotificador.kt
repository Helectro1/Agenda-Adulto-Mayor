package com.equipo2.agendaadultomayor.interfaces.servicios

enum class OrigenRecordatorio {
    ACTIVIDAD,
    CITA
}

data class iRecordatorio(
    val lIdOrigen: Long,
    val eOrigen: OrigenRecordatorio,
    val strTitulo: String,
    val dtDisparo: Long
)

interface iNotificador {
    fun Recordatorio_Programar(objRecordatorio: iRecordatorio)
    fun Recordatorio_Cancelar(lIdOrigen: Long, eOrigen: OrigenRecordatorio = OrigenRecordatorio.CITA)
}