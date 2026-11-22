package com.equipo2.agendaadultomayor.interfaces.datos

data class ActividadResumen(
    val lIdActividad: Long,
    val strNombre: String,
    val strDescripcion: String,
    val strHora: String, // Formato HH:mm
    val eTipo: String,
    val bActiva: Boolean = true
)

interface iActividadRepository {
    suspend fun obtenerActivas(): List<ActividadResumen>
}

// Implementación temporal mientras el módulo Registro se integra a main
class ActividadRepositoryTemporal : iActividadRepository {
    override suspend fun obtenerActivas(): List<ActividadResumen> {
        return listOf(
            ActividadResumen(
                lIdActividad = 1L,
                strNombre = "Tomar Losartán 50mg",
                strDescripcion = "Con un vaso de agua después del desayuno",
                strHora = "08:30",
                eTipo = "MEDICAMENTO"
            ),
            ActividadResumen(
                lIdActividad = 2L,
                strNombre = "Caminata ligera en patio",
                strDescripcion = "15 minutos acompañado",
                strHora = "17:00",
                eTipo = "CUIDADO"
            )
        )
    }
}