package com.example.cuidado

import android.app.Application
import com.example.cuidado.data.local.AppDatabase
import com.example.cuidado.data.repository.ActivityRepository

class CuidadoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Inicialización manual de la base de datos y el repositorio
        val database = AppDatabase.getDatabase(this)
        ActivityRepository.initialize(
            dao = database.activityDao(),
            logDao = database.activityLogDao()
        )
    }
}
