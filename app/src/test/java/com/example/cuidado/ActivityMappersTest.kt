package com.example.cuidado

import com.example.cuidado.data.local.ActivityEntity
import com.example.cuidado.data.local.ActivityLogEntity
import com.example.cuidado.data.local.toDomain
import com.example.cuidado.data.local.toEntity
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.model.ActivityLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ActivityMappersTest {

    // TC-MOD-001 (RF-001 / RNF-004): Mapeo bidireccional de ActivityEntity <-> Activity
    @Test
    fun `TC-MOD-001_activityEntity_toDomain_y_toEntity_mapea_correctamente_todos_los_campos`() {
        // Arrange
        val entity = ActivityEntity(
            id = "act-001",
            name = "Tomar Losartán 50mg",
            description = "Con un vaso de agua después del desayuno",
            time = "08:30",
            type = "Medicamento",
            repeatDays = "L,M,X,J,V,S,D",
            isActive = true,
            color = 0xFF4CAF50.toInt()
        )

        // Act
        val domain: Activity = entity.toDomain()
        val mappedBack: ActivityEntity = domain.toEntity()

        // Assert
        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.time, domain.time)
        assertEquals(entity.type, domain.type)
        assertEquals(entity.repeatDays, domain.repeatDays)
        assertEquals(entity.isActive, domain.isActive)
        assertEquals(entity.color, domain.color)
        assertEquals(entity, mappedBack)
    }

    // TC-MOD-001 (RF-001): Manejo de color nulo en Activity
    @Test
    fun `TC-MOD-001_activity_con_color_nulo_se_mapea_sin_errores`() {
        // Arrange
        val domain = Activity(
            id = "act-002",
            name = "Caminata ligera",
            description = "15 minutos en el patio",
            time = "17:00",
            type = "Cuidado",
            repeatDays = "L,X,V",
            isActive = true,
            color = null
        )

        // Act
        val entity = domain.toEntity()
        val domainBack = entity.toDomain()

        // Assert
        assertNull(entity.color)
        assertNull(domainBack.color)
        assertEquals(domain, domainBack)
    }

    // TC-MOD-003 (RF-003 / RF-012): Mapeo bidireccional de historial ActivityLogEntity <-> ActivityLog
    @Test
    fun `TC-MOD-003_activityLogEntity_toDomain_y_toEntity_mapea_estado_y_hora_ejecucion`() {
        // Arrange
        val logEntity = ActivityLogEntity(
            id = "log-100",
            activityId = "act-001",
            date = "2026-11-22",
            status = "REALIZADA",
            executionTime = "08:35"
        )

        // Act
        val domainLog: ActivityLog = logEntity.toDomain()
        val entityBack: ActivityLogEntity = domainLog.toEntity()

        // Assert
        assertEquals("log-100", domainLog.id)
        assertEquals("act-001", domainLog.activityId)
        assertEquals("2026-11-22", domainLog.date)
        assertEquals("REALIZADA", domainLog.status)
        assertEquals("08:35", domainLog.executionTime)
        assertEquals(logEntity, entityBack)
    }

    // TC-MOD-003 (RF-003): Manejo de executionTime nulo cuando la actividad está pendiente u omitida
    @Test
    fun `TC-MOD-003_activityLog_omitida_con_executionTime_nulo_se_mapea_correctamente`() {
        // Arrange
        val domainLog = ActivityLog(
            id = "log-101",
            activityId = "act-002",
            date = "2026-11-22",
            status = "OMITIDA",
            executionTime = null
        )

        // Act
        val entity = domainLog.toEntity()
        val domainBack = entity.toDomain()

        // Assert
        assertNull(entity.executionTime)
        assertEquals("OMITIDA", domainBack.status)
        assertEquals(domainLog, domainBack)
    }
}