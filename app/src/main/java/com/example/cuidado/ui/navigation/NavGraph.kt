package com.example.cuidado.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cuidado.data.local.AppDatabase
import com.example.cuidado.data.repository.CitaRepository
import com.example.cuidado.interfaces.datos.ActivityRepositoryAdapter
import com.example.cuidado.servicios.NotificacionScheduler
import com.example.cuidado.ui.screens.activities.ActivitiesScreen
import com.example.cuidado.ui.screens.activities.AddActivityScreen
import com.example.cuidado.ui.screens.agenda.CalendarioScreen
import com.example.cuidado.ui.screens.agenda.CitaFormScreen
import com.example.cuidado.ui.screens.agenda.CitaViewModel
import com.example.cuidado.ui.screens.agenda.CitaViewModelFactory
import com.example.cuidado.ui.screens.history.HistoryScreen
import com.example.cuidado.ui.screens.home.HomeScreen
import com.example.cuidado.ui.screens.mediciones.MedicionFormScreen
import com.example.cuidado.ui.screens.mediciones.MedicionesScreen
import com.example.cuidado.ui.screens.profile.ProfileOverviewScreen

object Routes {
    const val HOME = "home"
    const val ACTIVITIES = "activities"
    const val ADD_ACTIVITY = "add_activity"
    const val ADD_ACTIVITY_PARAM = "add_activity?activityId={activityId}"
    const val AGENDA = "agenda"
    const val NUEVA_CITA = "nueva_cita/{fecha}"
    const val NUEVA_MEDICION = "nueva_medicion"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val MEDICIONES = "mediciones"

    fun addActivity(activityId: String? = null): String {
        return if (activityId != null) "add_activity?activityId=$activityId" else "add_activity"
    }

    fun nuevaCita(fecha: Long): String {
        return "nueva_cita/$fecha"
    }
}

@Composable
fun CuidaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {

        composable(Routes.HOME) {
            HomeScreen(
                onAddActivity = {
                    navController.navigate(Routes.addActivity())
                },
                onAddCita = {
                    navController.navigate(Routes.nuevaCita(System.currentTimeMillis()))
                },
                onAddMedicion = {
                    navController.navigate(Routes.NUEVA_MEDICION)
                }
            )
        }

        composable(Routes.ACTIVITIES) {
            ActivitiesScreen(
                onAddActivity = {
                    navController.navigate(Routes.addActivity())
                },
                onEditActivity = { activityId ->
                    navController.navigate(Routes.addActivity(activityId))
                }
            )
        }

        composable(
            route = Routes.ADD_ACTIVITY_PARAM,
            arguments = listOf(navArgument("activityId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            AddActivityScreen(
                onSave = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.AGENDA) {
            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val citaRepository = remember { CitaRepository(database.citaDao()) }
            val actividadRepository = remember { ActivityRepositoryAdapter() }
            val notificador = remember { NotificacionScheduler(context) }

            val vmCita: CitaViewModel = viewModel(
                factory = CitaViewModelFactory(
                    objCitaRepository = citaRepository,
                    objActividadRepository = actividadRepository,
                    objNotificador = notificador,
                    objDatabase = database
                )
            )

            CalendarioScreen(
                vmCita = vmCita,
                onNavegarNuevaCita = { fechaSeleccionada ->
                    navController.navigate(Routes.nuevaCita(fechaSeleccionada))
                }
            )
        }

        composable(
            route = Routes.NUEVA_CITA,
            arguments = listOf(navArgument("fecha") { type = NavType.LongType })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getLong("fecha") ?: System.currentTimeMillis()
            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val citaRepository = remember { CitaRepository(database.citaDao()) }
            val actividadRepository = remember { ActivityRepositoryAdapter() }
            val notificador = remember { NotificacionScheduler(context) }

            val vmCita: CitaViewModel = viewModel(
                factory = CitaViewModelFactory(
                    objCitaRepository = citaRepository,
                    objActividadRepository = actividadRepository,
                    objNotificador = notificador,
                    objDatabase = database
                )
            )

            CitaFormScreen(
                vmCita = vmCita,
                dtFechaInicial = fecha,
                onRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen()
        }

        composable(Routes.MEDICIONES) {
            MedicionesScreen()
        }

        composable(Routes.NUEVA_MEDICION) {
            MedicionFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileOverviewScreen()
        }
    }
}
