package com.example.cuidado

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cuidado.data.local.ActivityLogDao
import com.example.cuidado.data.local.ActivityLogEntity
import com.example.cuidado.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityLogDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var activityLogDao: ActivityLogDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        activityLogDao = database.activityLogDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // TC-MOD-003 (RF-003): Marcar actividad como realizada, pendiente u omitida
    @Test
    fun tc_mod_003_insert_y_updateLog_actualiza_estado_de_actividad_en_historial() {
        // Arrange
        val logPendiente = ActivityLogEntity(
            id = "log-1",
            activityId = "act-1",
            date = "2026-11-22",
            status = "PENDIENTE",
            executionTime = null
        )
        activityLogDao.insertLog(logPendiente)

        // Act
        val logRealizada = logPendiente.copy(status = "REALIZADA", executionTime = "09:05")
        activityLogDao.updateLog(logRealizada)
        val retrieved = activityLogDao.getLogForActivityAndDate("act-1", "2026-11-22")

        // Assert
        assertNotNull(retrieved)
        assertEquals("REALIZADA", retrieved?.status)
        assertEquals("09:05", retrieved?.executionTime)
    }

    // TC-MOD-012 (RF-012): Consultar historial de actividades realizadas u omitidas en fechas anteriores
    @Test
    fun tc_mod_012_observeLogsForDate_y_ForActivity_filtran_historial_correctamente() = runBlocking {
        // Arrange
        val logAyer = ActivityLogEntity("log-1", "act-1", "2026-11-21", "REALIZADA", "08:00")
        val logHoy1 = ActivityLogEntity("log-2", "act-1", "2026-11-22", "OMITIDA", null)
        val logHoy2 = ActivityLogEntity("log-3", "act-2", "2026-11-22", "REALIZADA", "10:15")
        activityLogDao.insertLog(logAyer)
        activityLogDao.insertLog(logHoy1)
        activityLogDao.insertLog(logHoy2)

        // Act
        val logsHoy = activityLogDao.observeLogsForDate("2026-11-22").first()
        val historialAct1 = activityLogDao.observeLogsForActivity("act-1").first()

        // Assert
        assertEquals(2, logsHoy.size)
        assertEquals(2, historialAct1.size)

        // Act & Assert - Eliminación
        activityLogDao.deleteLog(logAyer)
        assertNull(activityLogDao.getLogForActivityAndDate("act-1", "2026-11-21"))
    }
}