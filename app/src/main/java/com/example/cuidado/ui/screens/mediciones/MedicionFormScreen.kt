package com.example.cuidado.ui.screens.mediciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicionFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: MedicionViewModel = viewModel()
) {
    var selectedTipo by remember { mutableStateOf("Glucosa") }
    var valorText by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val tipos = listOf("Glucosa", "PresionSistolica", "PresionDiastolica", "Peso", "Temperatura")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar nueva medición", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tipo de medición:", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tipos.take(3).forEach { tipo ->
                        FilterChip(
                            selected = selectedTipo == tipo,
                            onClick = { selectedTipo = tipo },
                            label = { Text(tipo, fontSize = 14.sp) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tipos.drop(3).forEach { tipo ->
                        FilterChip(
                            selected = selectedTipo == tipo,
                            onClick = { selectedTipo = tipo },
                            label = { Text(tipo, fontSize = 14.sp) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = valorText,
                onValueChange = { valorText = it },
                label = { Text("Valor", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Observaciones / Notas", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            message?.let {
                Text(it, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveMedicion(
                        tipo = selectedTipo,
                        valorStr = valorText,
                        notas = notas,
                        onSaved = {
                            onNavigateBack()
                        },
                        onError = { err ->
                            message = err
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Text("Guardar Medición", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
