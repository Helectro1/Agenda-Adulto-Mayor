package com.example.cuidado

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cuidado.data.local.AppDatabase
import com.example.cuidado.data.local.CaregiverEntity
import com.example.cuidado.data.local.ProfileDao
import com.example.cuidado.data.local.ProfileEntity
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
class ProfileDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var profileDao: ProfileDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        profileDao = database.profileDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // TC-MOD-009 (RF-009): Registro y edición de información de la persona dependiente
    @Test
    fun tc_mod_009_saveProfile_inserta_y_reemplaza_perfil_del_adulto_mayor() = runBlocking {
        // Arrange
        assertNull(profileDao.observeProfile().first())
        val initialProfile = ProfileEntity(
            id = 1,
            name = "Rosa María Pérez",
            age = 79,
            conditions = "Diabetes tipo 2",
            frequentMedications = "Metformina 850mg"
        )

        // Act - Registro inicial
        profileDao.saveProfile(initialProfile)
        val saved = profileDao.observeProfile().first()

        // Assert - Registro inicial
        assertNotNull(saved)
        assertEquals("Rosa María Pérez", saved?.name)
        assertEquals(79, saved?.age)

        // Act - Edición (OnConflictStrategy.REPLACE sobre id = 1)
        val updatedProfile = initialProfile.copy(age = 80, conditions = "Diabetes tipo 2 controlada")
        profileDao.saveProfile(updatedProfile)
        val replaced = profileDao.observeProfile().first()

        // Assert - Edición
        assertEquals(80, replaced?.age)
        assertEquals("Diabetes tipo 2 controlada", replaced?.conditions)
    }

    // TC-MOD-010 (RF-010): Guardado y lectura de datos de contacto del cuidador principal
    @Test
    fun tc_mod_010_saveCaregiver_guarda_y_actualiza_contacto_del_cuidador() = runBlocking {
        // Arrange
        val caregiver = CaregiverEntity(
            id = 1,
            name = "Carlos Pérez",
            phone = "3398765432"
        )

        // Act
        profileDao.saveCaregiver(caregiver)
        val observed = profileDao.observeCaregiver().first()

        // Assert
        assertNotNull(observed)
        assertEquals("Carlos Pérez", observed?.name)
        assertEquals("3398765432", observed?.phone)
    }
}