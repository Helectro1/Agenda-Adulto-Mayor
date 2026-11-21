package com.example.cuidado.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight

private enum class ProfileSection { PROFILE, CAREGIVER }

/**
 * Pantalla de la pestaña Perfil: muestra los datos guardados o pide
 * registrarlos si aún no existen (RF-009, RF-010).
 */
@Composable
fun ProfileOverviewScreen(viewModel: ProfileViewModel = viewModel()) {
    var editing by remember { mutableStateOf<ProfileSection?>(null) }

    BackHandler(enabled = editing != null) { editing = null }

    when (editing) {
        ProfileSection.PROFILE ->
            ProfileScreen(viewModel = viewModel, onSaved = { editing = null })
        ProfileSection.CAREGIVER ->
            CaregiverScreen(viewModel = viewModel, onSaved = { editing = null })
        null -> ProfileOverview(viewModel, onEdit = { editing = it })
    }
}

@Composable
private fun ProfileOverview(
    viewModel: ProfileViewModel,
    onEdit: (ProfileSection) -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val caregiver by viewModel.caregiver.collectAsState()
    val hasProfile = profile.name.isNotBlank()
    val hasCaregiver = caregiver.name.isNotBlank()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Perfil", style = MaterialTheme.typography.headlineSmall)

            InfoCard(
                title = "Adulto mayor",
                isEmpty = !hasProfile,
                emptyText = "Aún no hay un perfil registrado.",
                actionText = if (hasProfile) "Editar" else "Registrar perfil",
                onAction = { onEdit(ProfileSection.PROFILE) }
            ) {
                InfoRow("Nombre", profile.name)
                InfoRow("Edad", "${profile.age} años")
                InfoRow("Padecimientos", profile.conditions.ifBlank { "—" })
                InfoRow("Medicamentos frecuentes", profile.frequentMedications.ifBlank { "—" })
            }

            InfoCard(
                title = "Cuidador principal",
                isEmpty = !hasCaregiver,
                emptyText = "Aún no hay un cuidador registrado.",
                actionText = if (hasCaregiver) "Editar" else "Registrar cuidador",
                onAction = { onEdit(ProfileSection.CAREGIVER) }
            ) {
                InfoRow("Nombre", caregiver.name)
                InfoRow("Teléfono", caregiver.phone.ifBlank { "—" })
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    isEmpty: Boolean,
    emptyText: String,
    actionText: String,
    onAction: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (isEmpty) Text(emptyText, fontSize = 16.sp) else content()
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(actionText, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label.uppercase(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(value, fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}