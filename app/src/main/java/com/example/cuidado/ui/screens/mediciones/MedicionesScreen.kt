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
import com.example.cuidado.data.model.RangoMedicion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicionesScreen(viewModel: MedicionViewModel = viewModel()) {
    val mediciones by viewModel.mediciones.collectAsState()
    val alertasActivas by viewModel.alertasActivas.collectAsState()
    val rangos by viewModel.rangosState.collectAsState()

    var selectedTipo by remember { mutableStateOf("Glucosa") }
    var valorText by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    var showConfigDialog by remember { mutableStateOf(false) }

    val tipos = listOf("Glucosa", "PresionSistolica", "PresionDiastolica", "Peso", "Temperatura")

    val currentRango = rangos[selectedTipo] ?: when (selectedTipo.lowercase()) {
        "glucosa" -> RangoMedicion(selectedTipo, 70.0, 140.0, 3)
        "presionsistolica" -> RangoMedicion(selectedTipo, 90.0, 130.0, 3)
        "presiondiastolica" -> RangoMedicion(selectedTipo, 60.0, 85.0, 3)
        "peso" -> RangoMedicion(selectedTipo, 40.0, 120.0, 5)
        "temperatura" -> RangoMedicion(selectedTipo, 36.0, 37.5, 2)
        else -> RangoMedicion(selectedTipo, 0.0, 100.0, 3)
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
            // Tarjetas de Alerta Preventiva en tiempo real
            if (alertasActivas.isNotEmpty()) {
                alertasActivas.forEach { alerta ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = alerta.mensaje,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Aviso informativo: esta aplicación no emite diagnósticos médicos; se sugiere consultar con su médico.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

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

            // Botón de Configuración de Rango y Umbral N
            OutlinedButton(
                onClick = { showConfigDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = "Configurar rango y alerta: ${currentRango.valorMin} - ${currentRango.valorMax} | Alerta tras ${currentRango.umbralConsecutivo} registros",
                    fontSize = 14.sp
                )
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

    if (showConfigDialog) {
        RangoConfigDialog(
            tipo = selectedTipo,
            initialMin = currentRango.valorMin.toString(),
            initialMax = currentRango.valorMax.toString(),
            initialUmbral = currentRango.umbralConsecutivo.toString(),
            onDismiss = { showConfigDialog = false },
            onSave = { min, max, umbral ->
                viewModel.guardarConfiguracionRango(
                    tipo = selectedTipo,
                    minStr = min,
                    maxStr = max,
                    umbralStr = umbral,
                    onSaved = { showConfigDialog = false },
                    onError = { message = it }
                )
            }
        )
    }
}

@Composable
private fun RangoConfigDialog(
    tipo: String,
    initialMin: String,
    initialMax: String,
    initialUmbral: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var minText by remember { mutableStateOf(initialMin) }
    var maxText by remember { mutableStateOf(initialMax) }
    var umbralText by remember { mutableStateOf(initialUmbral) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar rango para $tipo", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Establece los valores normales y el umbral N de registros consecutivos fuera de rango.", fontSize = 14.sp)
                OutlinedTextField(
                    value = minText,
                    onValueChange = { minText = it },
                    label = { Text("Valor Mínimo Normal") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxText,
                    onValueChange = { maxText = it },
                    label = { Text("Valor Máximo Normal") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = umbralText,
                    onValueChange = { umbralText = it },
                    label = { Text("Umbral N (registros consecutivos)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(minText, maxText, umbralText)
                }
            ) {
                Text("Guardar Configuración")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
