package com.example.cuidado.ui.screens.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuidado.data.model.formatActivityTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val nombresMeses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val colorComplete = Color(0xFF2E7D32)
    val colorPartial = Color(0xFFF9A825)
    val colorUnstarted = Color(0xFFC62828)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Actividades", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            var m = uiState.month - 1
                            var y = uiState.year
                            if (m < 1) {
                                m = 12
                                y -= 1
                            }
                            viewModel.changeMonth(y, m)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
                    }

                    Text(
                        text = "${nombresMeses[uiState.month - 1]} ${uiState.year}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            var m = uiState.month + 1
                            var y = uiState.year
                            if (m > 12) {
                                m = 1
                                y += 1
                            }
                            viewModel.changeMonth(y, m)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
                    }
                }
            }

            // Calendar grid
            item {
                MonthCalendarGrid(
                    year = uiState.year,
                    month = uiState.month,
                    selectedDay = uiState.selectedDay,
                    daysProgress = uiState.daysProgress,
                    onDayClick = { day -> viewModel.selectDay(day) }
                )
            }

            // Legend
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = colorComplete, text = "100% Completo")
                    LegendItem(color = colorPartial, text = "Parcial")
                    LegendItem(color = colorUnstarted, text = "0% / Sin empezar")
                }
            }

            // Selected day details header
            item {
                Text(
                    text = "Detalle del día ${uiState.selectedDay} de ${nombresMeses[uiState.month - 1]}",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.isBeforeStart) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "El seguimiento aún no había iniciado en esta fecha.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else if (uiState.dayDetails.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No hay actividades programadas para este día.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(uiState.dayDetails) { detail ->
                    HistoryDetailCard(detail = detail)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun MonthCalendarGrid(
    year: Int,
    month: Int,
    selectedDay: Int,
    daysProgress: List<DayProgress>,
    onDayClick: (Int) -> Unit
) {
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val cal = Calendar.getInstance().apply { set(year, month - 1, 1) }
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Mon=0, Tue=1... Sun=6
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val celdas = mutableListOf<Int?>()
    repeat(firstDayOfWeek) { celdas.add(null) }
    for (d in 1..maxDays) { celdas.add(d) }
    while (celdas.size % 7 != 0) { celdas.add(null) }

    val progressMap = daysProgress.associateBy { it.day }

    val colorComplete = Color(0xFF2E7D32)
    val colorPartial = Color(0xFFF9A825)
    val colorUnstarted = Color(0xFFC62828)
    val colorTrack = Color.LightGray.copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            diasSemana.forEach { nombre ->
                Text(
                    text = nombre,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        celdas.chunked(7).forEach { semana ->
            Row(modifier = Modifier.fillMaxWidth()) {
                semana.forEach { dia ->
                    if (dia == null) {
                        Box(modifier = Modifier.weight(1f).height(48.dp))
                    } else {
                        val progressObj = progressMap[dia]
                        val isSelected = dia == selectedDay
                        val status = progressObj?.status
                        val showCircle = status == DayStatus.COMPLETE || status == DayStatus.PARCIAL || status == DayStatus.UNSTARTED

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { onDayClick(dia) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (showCircle && progressObj != null) {
                                val prog = progressObj.progress

                                Canvas(modifier = Modifier.size(36.dp)) {
                                    val strokeWidth = 3.dp.toPx()
                                    when (status) {
                                        DayStatus.COMPLETE -> {
                                            drawCircle(color = colorComplete, radius = size.minDimension / 2f - strokeWidth / 2f, style = Stroke(width = strokeWidth))
                                        }
                                        DayStatus.PARCIAL -> {
                                            drawCircle(color = colorTrack, radius = size.minDimension / 2f - strokeWidth / 2f, style = Stroke(width = strokeWidth))
                                            drawArc(
                                                color = colorPartial,
                                                startAngle = -90f,
                                                sweepAngle = 360f * prog,
                                                useCenter = false,
                                                style = Stroke(width = strokeWidth)
                                            )
                                        }
                                        DayStatus.UNSTARTED -> {
                                            drawCircle(color = colorUnstarted, radius = size.minDimension / 2f - strokeWidth / 2f, style = Stroke(width = strokeWidth))
                                        }
                                        else -> {}
                                    }
                                }
                            }

                            Text(
                                text = dia.toString(),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (showCircle || isSelected) {
                                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = text, fontSize = 12.sp)
    }
}

@Composable
private fun HistoryDetailCard(detail: DayActivityDetail) {
    val containerColor = when (detail.status) {
        "COMPLETED" -> MaterialTheme.colorScheme.primaryContainer
        "SKIPPED", "OMITTED" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${detail.activity.name} (${formatActivityTime(detail.activity.time)})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Estado: ${detail.status}",
                fontSize = 16.sp
            )
            if (!detail.executionTime.isNullOrBlank()) {
                Text(
                    text = "Hora de registro: ${detail.executionTime}",
                    fontSize = 16.sp
                )
            }
            if (!detail.notes.isNullOrBlank()) {
                Text(
                    text = "Notas: ${detail.notes}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
