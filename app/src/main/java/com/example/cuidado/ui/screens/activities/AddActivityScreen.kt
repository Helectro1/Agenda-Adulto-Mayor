package com.example.cuidado.ui.screens.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuidado.R
import com.example.cuidado.ui.components.CuidaFormScaffold
import com.example.cuidado.ui.components.CuidaTimePickerDialog
import java.util.Locale

@Composable
fun AddActivityScreen(
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: AddActivityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    val days = listOf(
        "L", "M", "X", "J", "V", "S", "D"
    )

    CuidaFormScaffold(
        title = if (uiState.isEditing) "Editar actividad" else stringResource(R.string.new_activity),
        onNavigateBack = onCancel,
        onSave = {
            viewModel.saveActivity()
            onSave()
        },
        saveButtonText = stringResource(R.string.save),
        isSaveEnabled = uiState.isFormValid
    ) {
        Text(
            text = stringResource(R.string.name_label)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = {
                viewModel.onNameChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(stringResource(R.string.name_placeholder))
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Modo de programación",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !uiState.isInterval,
                onClick = { viewModel.onIntervalModeChange(false) },
                label = { Text("Hora fija del día") },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            )
            FilterChip(
                selected = uiState.isInterval,
                onClick = { viewModel.onIntervalModeChange(true) },
                label = { Text("Cada cierto tiempo") },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = if (uiState.isInterval) "Intervalo (Horas y Minutos)" else stringResource(R.string.hour_label)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AccessTime, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (uiState.hour.isNotBlank()) {
                    if (uiState.isInterval) "Cada ${uiState.hour}" else "Hora: ${uiState.hour}"
                } else {
                    if (uiState.isInterval) "Seleccionar intervalo *" else "Seleccionar hora *"
                },
                fontSize = 18.sp
            )
        }

        if (showTimePicker) {
            val parts = uiState.hour.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: if (uiState.isInterval) 2 else 8
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            CuidaTimePickerDialog(
                initialHour = h,
                initialMinute = m,
                title = if (uiState.isInterval) "Seleccionar intervalo" else "Seleccionar hora",
                onDismiss = { showTimePicker = false },
                onConfirm = { hourOfDay, minute ->
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    viewModel.onHourChange(formattedTime)
                    showTimePicker = false
                }
            )
        }

        if (!uiState.isInterval) {
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = stringResource(R.string.repeat_label)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                days.forEach { day ->

                    FilterChip(
                        selected = day in uiState.selectedDays,
                        onClick = {
                            viewModel.toggleDay(day)
                        },
                        label = {
                            Text(day)
                        }
                    )
                }
            }
        }
    }
}
