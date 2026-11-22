package com.example.cuidado.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cuidado.ui.navigation.Routes

data class BottomNavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val items = listOf(
        BottomNavigationItem(
            route = Routes.HOME,
            label = "Inicio",
            icon = Icons.Default.Home
        ),
        BottomNavigationItem(
            route = Routes.ACTIVITIES,
            label = "Actividades",
            icon = Icons.Default.List
        ),
        BottomNavigationItem(
            route = Routes.HISTORY,
            label = "Historial",
            icon = Icons.Default.History
        ),
        BottomNavigationItem(
            route = Routes.PROFILE,
            label = "Perfil",
            icon = Icons.Default.Person
        )
    )

    val navBackStackEntry =
        navController.currentBackStackEntryAsState()

    val currentDestination =
        navBackStackEntry.value?.destination

    NavigationBar(
        modifier = modifier
    ) {

        items.forEach { item ->

            NavigationBarItem(
                selected = currentDestination
                    ?.hierarchy
                    ?.any {
                        it.route == item.route
                    } == true,

                onClick = {

                    navController.navigate(item.route) {

                        popUpTo(Routes.HOME) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },

                label = {
                    Text(item.label)
                }
            )
        }
    }
}