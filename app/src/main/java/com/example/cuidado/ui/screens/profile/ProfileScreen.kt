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

/**
 * Pantalla del perfil del adulto mayor (RF-009).
 */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val profile by viewModel.profile.collectAsState()

    var name by remember(profile) { mutableStateOf(profile.name) }
    var ageText by remember(profile) {
        mutableStateOf(if (profile.age > 0) profile.age.toString() else "")
    }
    var conditions by remember(profile) { mutableStateOf(profile.conditions) }
    var medications by remember(profile) { mutableStateOf(profile.frequentMedications) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Perfil del adulto mayor", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre", fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = ageText,
            onValueChange = { ageText = it.filter { c -> c.isDigit() } },
            label = { Text("Edad", fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = conditions,
            onValueChange = { conditions = it },
            label = { Text("Padecimientos", fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        OutlinedTextField(
            value = medications,
            onValueChange = { medications = it },
            label = { Text("Medicamentos frecuentes", fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            onClick = {
                val age = ageText.toIntOrNull()
                if (age == null) {
                    message = "Escribe una edad válida"
                } else {
                    viewModel.saveProfile(
                        name = name,
                        age = age,
                        conditions = conditions,
                        frequentMedications = medications,
                        onSaved = { message = "Perfil guardado" },
                        onError = { message = it }
                    )
                }
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