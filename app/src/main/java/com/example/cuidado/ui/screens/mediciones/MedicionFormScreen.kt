package com.example.cuidado.ui.screens.mediciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuidado.ui.components.CuidaFormScaffold

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

    CuidaFormScaffold(
        title = "Registrar nueva medición",
        onNavigateBack = onNavigateBack,
        onSave = {
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
        saveButtonText = "Guardar Medición"
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
    }
}
