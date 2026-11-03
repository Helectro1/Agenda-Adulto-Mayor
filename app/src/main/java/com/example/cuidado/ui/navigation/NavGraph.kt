package com.example.cuidado.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cuidado.ui.screens.activities.ActivitiesScreen
import com.example.cuidado.ui.screens.activities.AddActivityScreen
import com.example.cuidado.ui.screens.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val ACTIVITIES = "activities"
    const val ADD_ACTIVITY = "add_activity"
    const val HISTORY = "history"
    const val PROFILE = "profile"
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
            HomeScreen()
        }

        composable(Routes.ACTIVITIES) {
            ActivitiesScreen(
                onAddActivity = {
                    navController.navigate(Routes.ADD_ACTIVITY)
                }
            )
        }

        composable(Routes.ADD_ACTIVITY) {
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

        composable(Routes.PROFILE) {
            PlaceholderScreen(
                title = "Perfil"
            )
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