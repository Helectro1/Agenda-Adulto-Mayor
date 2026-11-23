package com.example.cuidado

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cuidado.data.local.ActivityDao
import com.example.cuidado.data.local.ActivityEntity
import com.example.cuidado.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var activityDao: ActivityDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        activityDao = database.activityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // TC-MOD-001 (RF-001): Creación/inserción de nueva actividad en Room
    @Test
    fun tc_mod_001_insertActivity_guarda_y_recupera_actividad_correctamente() {
        // Arrange
        val activity = ActivityEntity(
            id = "act-1",
            name = "Medicamento mañana",
            description = "Metformina 850mg",
            time = "09:00",
            type = "Medicamento",
            repeatDays = "L,M,X,J,V",
            isActive = true,
            color = 12345
        )

        // Act
        val insertedRowId = activityDao.insertActivity(activity)
        val allActivities = activityDao.getAllActivities()

        // Assert
        assertTrue(insertedRowId > 0)
        assertEquals(1, allActivities.size)
        assertEquals(activity, allActivities.first())
    }

    // TC-MOD-002 (RF-002): Consulta ordenada por horario (ORDER BY time ASC)
    @Test
    fun tc_mod_002_observeActivities_devuelve_actividades_ordenadas_por_horario() = runBlocking {
        // Arrange
        val tarde = ActivityEntity("act-2", "Merienda", "Fruta picada", "16:00", "General", "D", true, null)
        val manana = ActivityEntity("act-1", "Desayuno", "Avena", "08:00", "General", "D", true, null)
        val noche = ActivityEntity("act-3", "Pastilla dormir", "Melatonina", "21:30", "Medicamento", "D", true, null)
        activityDao.insertActivities(listOf(tarde, manana, noche))

        // Act
        val observedList = activityDao.observeActivities().first()

        // Assert
        assertEquals(3, observedList.size)
        assertEquals("08:00", observedList[0].time)
        assertEquals("16:00", observedList[1].time)
        assertEquals("21:30", observedList[2].time)
    }

    // TC-MOD-005 (RF-005): Edición y eliminación de actividades previamente programadas
    @Test
    fun tc_mod_005_update_y_deleteActivity_modifican_y_eliminan_registro() {
        // Arrange
        val original = ActivityEntity("act-10", "Terapia física", "Ejercicios de brazo", "11:00", "Cuidado", "L,V", true, null)
        activityDao.insertActivity(original)

        // Act - Edición
        val edited = original.copy(name = "Terapia física avanzada", time = "12:00")
        val updatedRows = activityDao.updateActivity(edited)
        val afterUpdate = activityDao.getAllActivities().first()

        // Assert - Edición
        assertEquals(1, updatedRows)
        assertEquals("Terapia física avanzada", afterUpdate.name)
        assertEquals("12:00", afterUpdate.time)

        // Act - Eliminación
        val deletedRows = activityDao.deleteActivity(edited)
        val afterDelete = activityDao.getAllActivities()

        // Assert - Eliminación
        assertEquals(1, deletedRows)
        assertTrue(afterDelete.isEmpty())
    }
}