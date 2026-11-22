package com.equipo2.agendaadultomayor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.equipo2.agendaadultomayor.data.local.AppDatabase
import com.equipo2.agendaadultomayor.data.repository.CitaRepository
import com.equipo2.agendaadultomayor.interfaces.datos.ActividadRepositoryTemporal
import com.equipo2.agendaadultomayor.servicios.NotificacionScheduler
import com.equipo2.agendaadultomayor.ui.agenda.CalendarioScreen
import com.equipo2.agendaadultomayor.ui.agenda.CitaFormScreen
import com.equipo2.agendaadultomayor.ui.theme.AgendaAdultoMayorTheme
import com.equipo2.agendaadultomayor.viewmodel.CitaViewModel
import com.equipo2.agendaadultomayor.viewmodel.CitaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgendaAdultoMayorTheme {
                val context = LocalContext.current

                // Solicitar permiso de notificaciones en Android 13+ (TIRAMISU)
                val permisoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { }
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val tienePermiso = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!tienePermiso) {
                            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Inicialización de dependencias del módulo Agenda
                val database = remember { AppDatabase.BaseDatos_ObtenerInstancia(context) }
                val citaRepository = remember { CitaRepository(database.citaDao()) }
                val actividadRepository = remember { ActividadRepositoryTemporal() }
                val notificador = remember { NotificacionScheduler(context) }

                val vmCita: CitaViewModel = viewModel(
                    factory = CitaViewModelFactory(
                        objCitaRepository = citaRepository,
                        objActividadRepository = actividadRepository,
                        objNotificador = notificador,
                        objDatabase = database
                    )
                )

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "calendario"
                ) {
                    composable("calendario") {
                        CalendarioScreen(
                            vmCita = vmCita,
                            onNavegarNuevaCita = { fechaSeleccionada ->
                                navController.navigate("nueva_cita/$fechaSeleccionada")
                            }
                        )
                    }

                    composable(
                        route = "nueva_cita/{fecha}",
                        arguments = listOf(navArgument("fecha") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val fecha = backStackEntry.arguments?.getLong("fecha")
                            ?: System.currentTimeMillis()
                        CitaFormScreen(
                            vmCita = vmCita,
                            dtFechaInicial = fecha,
                            onRegresar = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}