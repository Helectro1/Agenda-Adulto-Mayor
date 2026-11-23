package com.example.cuidado

import com.example.cuidado.data.local.CaregiverEntity
import com.example.cuidado.data.local.ProfileEntity
import com.example.cuidado.data.local.toDomain
import com.example.cuidado.data.local.toEntity
import com.example.cuidado.data.model.Caregiver
import com.example.cuidado.data.model.Profile
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileMappersTest {

    // TC-MOD-009 (RF-009): Mapeo bidireccional de datos de la persona dependiente
    @Test
    fun `TC-MOD-009_profileEntity_y_profile_se_mapean_bidireccionalmente_con_id_1`() {
        // Arrange
        val entity = ProfileEntity(
            id = 1,
            name = "Don Roberto Gómez",
            age = 82,
            conditions = "Hipertensión arterial, artrosis leve",
            frequentMedications = "Losartán 50mg, Paracetamol 500mg"
        )

        // Act
        val domain: Profile = entity.toDomain()
        val entityBack: ProfileEntity = domain.toEntity()

        // Assert
        assertEquals("Don Roberto Gómez", domain.name)
        assertEquals(82, domain.age)
        assertEquals("Hipertensión arterial, artrosis leve", domain.conditions)
        assertEquals("Losartán 50mg, Paracetamol 500mg", domain.frequentMedications)
        assertEquals(1, entityBack.id)
        assertEquals(entity, entityBack)
    }

    // TC-MOD-010 (RF-010): Mapeo bidireccional de información del cuidador principal
    @Test
    fun `TC-MOD-010_caregiverEntity_y_caregiver_se_mapean_bidireccionalmente_con_id_1`() {
        // Arrange
        val caregiver = Caregiver(
            name = "María Elena Gómez",
            phone = "3312345678"
        )

        // Act
        val entity: CaregiverEntity = caregiver.toEntity()
        val domainBack: Caregiver = entity.toDomain()

        // Assert
        assertEquals(1, entity.id)
        assertEquals("María Elena Gómez", entity.name)
        assertEquals("3312345678", entity.phone)
        assertEquals(caregiver, domainBack)
    }
}