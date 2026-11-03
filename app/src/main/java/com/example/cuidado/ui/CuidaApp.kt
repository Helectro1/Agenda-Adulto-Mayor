package com.example.cuidado.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cuidado.ui.components.BottomNavigationBar
import com.example.cuidado.ui.navigation.CuidaNavGraph
import com.example.cuidado.ui.navigation.Routes

@Composable
fun CuidaApp() {

    val navController = rememberNavController()

    val navBackStackEntry =
        navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry.value?.destination?.route

    val showBottomBar =
        currentRoute != Routes.ADD_ACTIVITY

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController
                )
            }
        }
    ) { innerPadding ->

        CuidaNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}