package com.example.cuidado.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cuidado.ui.screens.activities.ActivitiesScreen
import com.example.cuidado.ui.screens.activities.AddActivityScreen
import com.example.cuidado.ui.screens.home.HomeScreen
import com.example.cuidado.ui.screens.mediciones.MedicionesScreen
import com.example.cuidado.ui.screens.profile.ProfileOverviewScreen

object Routes {
    const val HOME = "home"
    const val ACTIVITIES = "activities"
    const val ADD_ACTIVITY = "add_activity"
    const val ADD_ACTIVITY_PARAM = "add_activity?activityId={activityId}"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val MEDICIONES = "mediciones"

    fun addActivity(activityId: String? = null): String {
        return if (activityId != null) "add_activity?activityId=$activityId" else "add_activity"
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
                }
            )
        }

        composable(Routes.HISTORY) {
            PlaceholderScreen(
                title = "Historial"
            )
        }

        composable(Routes.MEDICIONES) {
            MedicionesScreen()
        }

        composable(Routes.PROFILE) {
            ProfileOverviewScreen()
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title
        )
    }
}
