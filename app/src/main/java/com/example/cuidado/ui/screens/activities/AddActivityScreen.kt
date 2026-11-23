package com.example.cuidado.ui.screens.activities

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuidado.R
import com.example.cuidado.ui.components.CuidaFormScaffold
import java.util.Calendar
import java.util.Locale

@Composable
fun AddActivityScreen(
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: AddActivityViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

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
            text = stringResource(R.string.hour_label)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick = {
                val cal = Calendar.getInstance()
                val initialHour = if (uiState.hour.isNotBlank()) {
                    val parts = uiState.hour.split(":")
                    parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.HOUR_OF_DAY)
                } else {
                    cal.get(Calendar.HOUR_OF_DAY)
                }
                val initialMinute = if (uiState.hour.isNotBlank()) {
                    val parts = uiState.hour.split(":")
                    parts.getOrNull(1)?.toIntOrNull() ?: cal.get(Calendar.MINUTE)
                } else {
                    cal.get(Calendar.MINUTE)
                }

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                        viewModel.onHourChange(formattedTime)
                    },
                    initialHour,
                    initialMinute,
                    true
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AccessTime, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (uiState.hour.isNotBlank()) "Hora: ${uiState.hour}" else "Seleccionar hora *",
                fontSize = 18.sp
            )
        }

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
