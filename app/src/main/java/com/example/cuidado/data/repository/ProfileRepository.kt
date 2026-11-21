package com.example.cuidado.data.repository

import com.example.cuidado.data.local.ProfileDao
import com.example.cuidado.data.local.toDomain
import com.example.cuidado.data.local.toEntity
import com.example.cuidado.data.model.Caregiver
import com.example.cuidado.data.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repositorio para el perfil del adulto mayor y el cuidador principal.
 * Por el momento solo existe un perfil y un cuidador por instalación.
 */
object ProfileRepository {

    private lateinit var profileDao: ProfileDao

    fun initialize(dao: ProfileDao) {
        profileDao = dao
    }

    fun getProfile(): Flow<Profile?> =
        profileDao.observeProfile().map { it?.toDomain() }

    suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.IO) {
        profileDao.saveProfile(profile.toEntity())
    }

    fun getCaregiver(): Flow<Caregiver?> =
        profileDao.observeCaregiver().map { it?.toDomain() }

    suspend fun saveCaregiver(caregiver: Caregiver) = withContext(Dispatchers.IO) {
        profileDao.saveCaregiver(caregiver.toEntity())
    }
}