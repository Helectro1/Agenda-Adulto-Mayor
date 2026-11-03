package com.example.cuidado.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.cuidado.data.model.Activity

@Composable
fun ActivityCard(
    activity: Activity,
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    hasWarning: Boolean = false,
    showStatus: Boolean = false,
    onOptionClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            if (showStatus) {
                // Indicador del estado (Vista de Inicio)
                when {
                    hasWarning -> {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Atención",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    isCompleted -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Realizado",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    MaterialTheme.colorScheme.outlineVariant,
                                ),
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }

            // Información de la actividad
            Column(
                modifier = Modifier.weight(1f),
            ) {

                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = activity.time,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (!showStatus && activity.repeatDays.isNotEmpty()) {
                    // Mapeo simple de vuelta a nombres de días para mostrar en la tarjeta de gestión
                    val dayNames = mapOf(
                        "1" to "L", "2" to "M", "3" to "X", "4" to "J", "5" to "V", "6" to "S", "7" to "D"
                    )
                    val displayRepeat = activity.repeatDays.split(",")
                        .mapNotNull { dayNames[it] }
                        .joinToString(", ")
                    
                    Text(
                        text = displayRepeat,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Estado o Menú
            if (showStatus && isCompleted) {
                Text(
                    text = "Realizado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else if (onOptionClick != null) {
                IconButton(onClick = onOptionClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }
            }
        }
    }
}
