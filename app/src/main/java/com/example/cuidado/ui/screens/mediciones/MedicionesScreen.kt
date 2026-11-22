package com.example.cuidado.ui.screens.mediciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun MedicionesScreen(viewModel: MedicionViewModel = viewModel()) {
    val mediciones by viewModel.mediciones.collectAsState()

    var selectedTipo by remember { mutableStateOf("Glucosa") }
    var valorText by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var alertaGlucosa by remember { mutableStateOf<String?>(null) }

    val tipos = listOf("Glucosa", "PresionSistolica", "PresionDiastolica", "Peso", "Temperatura")

    LaunchedEffect(mediciones) {
        val alerta = viewModel.evaluarAlertaParaTipo("Glucosa")
        if (alerta.isAlertActive) {
            alertaGlucosa = alerta.mensaje
        } else {
            alertaGlucosa = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mediciones y Alertas", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registrar nueva medición", style = MaterialTheme.typography.titleLarge, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Selector de tipo (Chips)
            Text("Tipo de medición:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipos.take(3).forEach { tipo ->
                    FilterChip(
                        selected = selectedTipo == tipo,
                        onClick = { selectedTipo = tipo },
                        label = { Text(tipo, fontSize = 16.sp) },
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
                        label = { Text(tipo, fontSize = 16.sp) },
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
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
                label = { Text("Notas u observaciones", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    viewModel.saveMedicion(
                        tipo = selectedTipo,
                        valorStr = valorText,
                        notas = notas,
                        onSaved = {
                            valorText = ""
                            notas = ""
                            message = "Medición guardada correctamente"
                        },
                        onError = { message = it }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Guardar Medición", fontSize = 18.sp)
            }

            message?.let {
                Text(it, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }

            if (alertaGlucosa != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = alertaGlucosa!!,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Historial de Mediciones", style = MaterialTheme.typography.titleLarge, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            if (mediciones.isEmpty()) {
                Text("Aún no hay mediciones registradas.", fontSize = 16.sp)
            } else {
                mediciones.forEach { m ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${m.tipo}: ${m.valor}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Fecha: ${m.fecha} - ${m.hora}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (m.notas.isNotBlank()) {
                                Text("Notas: ${m.notas}", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
