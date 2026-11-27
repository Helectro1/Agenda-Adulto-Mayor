package com.example.cuidado.data.repository

import android.content.Context
import com.example.cuidado.data.local.ProfileDao
import com.example.cuidado.data.local.toDomain
import com.example.cuidado.data.local.toEntity
import com.example.cuidado.data.model.Caregiver
import com.example.cuidado.data.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repositorio para el perfil del adulto mayor y el cuidador principal.
 * Por el momento solo existe un perfil y un cuidador por instalación.
 */
object ProfileRepository {

    private lateinit var profileDao: ProfileDao
    private var appContext: Context? = null

    fun initialize(context: Context, dao: ProfileDao) {
        appContext = context.applicationContext
        profileDao = dao
        initProfileDateIfMissing()
    }

    fun initialize(dao: ProfileDao) {
        profileDao = dao
    }

    private fun initProfileDateIfMissing() {
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences("cuida_prefs", Context.MODE_PRIVATE)
            if (!prefs.contains("profile_created_date")) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.format(Date())
                prefs.edit().putString("profile_created_date", today).apply()
            }
        } catch (_: Exception) {}
    }

    fun getProfileStartDate(): String {
        val ctx = appContext
        if (ctx != null) {
            try {
                val prefs = ctx.getSharedPreferences("cuida_prefs", Context.MODE_PRIVATE)
                val date = prefs.getString("profile_created_date", null)
                if (!date.isNullOrBlank()) return date
            } catch (_: Exception) {}
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getProfile(): Flow<Profile?> =
        profileDao.observeProfile().map { it?.toDomain() }

    suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.IO) {
        val ctx = appContext
        if (ctx != null) {
            try {
                val prefs = ctx.getSharedPreferences("cuida_prefs", Context.MODE_PRIVATE)
                if (!prefs.contains("profile_created_date")) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = dateFormat.format(Date())
                    prefs.edit().putString("profile_created_date", today).apply()
                }
            } catch (_: Exception) {}
        }
        profileDao.saveProfile(profile.toEntity())
    }

    fun getCaregiver(): Flow<Caregiver?> =
        profileDao.observeCaregiver().map { it?.toDomain() }

    suspend fun saveCaregiver(caregiver: Caregiver) = withContext(Dispatchers.IO) {
        profileDao.saveCaregiver(caregiver.toEntity())
    }
}
