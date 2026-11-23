package com.example.cuidado.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Surface

/**
 * Pantalla del cuidador principal (RF-010).
 */
@Composable
fun CaregiverScreen(viewModel: ProfileViewModel = viewModel(), onSaved: () -> Unit = {} ) {
    val caregiver by viewModel.caregiver.collectAsState()

    var name by remember(caregiver) { mutableStateOf(caregiver.name) }
    var phone by remember(caregiver) { mutableStateOf(caregiver.phone) }
    var message by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Cuidador principal", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Button(
                onClick = {
                    viewModel.saveCaregiver(
                        name = name,
                        phone = phone,
                        onSaved = { message = "Cuidador guardado"; onSaved() },
                        onError = { message = it }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text("Guardar", fontSize = 18.sp)
            }

            message?.let { Text(it, fontSize = 16.sp) }
        }
    }
}