package com.example.cuidado.ui.screens.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuidado.R
import com.example.cuidado.data.model.Activity
import com.example.cuidado.ui.components.ActivityCard

@Composable
fun ActivitiesScreen(
    onAddActivity: () -> Unit,
    onEditActivity: (String) -> Unit,
    viewModel: ActivitiesViewModel = viewModel()
) {
    val activities by viewModel.activities.collectAsState()
    var activityToDelete by remember { mutableStateOf<Activity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddActivity
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_activity)
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = stringResource(R.string.activities),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.maria_routine),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }

            items(activities) { activity ->

                ActivityCard(
                    activity = activity,
                    onEdit = { onEditActivity(activity.id) },
                    onDelete = { activityToDelete = activity }
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }

    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text("Eliminar actividad", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de eliminar '${activityToDelete?.name}'? Esta acción no se puede deshacer.", fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        activityToDelete?.let { viewModel.deleteActivity(it) }
                        activityToDelete = null
                    }
                ) {
                    Text("Eliminar", fontSize = 16.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { activityToDelete = null }
                ) {
                    Text("Cancelar", fontSize = 16.sp)
                }
            }
        )
    }
}
