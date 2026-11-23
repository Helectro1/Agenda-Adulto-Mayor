package com.example.cuidado.ui.screens.agenda

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidado.R
import com.example.cuidado.data.local.CitaEntity
import com.example.cuidado.interfaces.datos.ActividadResumen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    vmCita: CitaViewModel,
    onNavegarNuevaCita: (Long) -> Unit
) {
    val estadoUi by vmCita.objEstadoUi.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estadoUi.strMensajeFeedback) {
        estadoUi.strMensajeFeedback?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            vmCita.limpiarMensaje()
        }
    }

    val nombresMeses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val diasConCitas = remember(estadoUi.lstCitasDelMes) {
        estadoUi.lstCitasDelMes.map { cita ->
            Calendar.getInstance().apply { timeInMillis = cita.dtFechaHora }.get(Calendar.DAY_OF_MONTH)
        }.toSet()
    }

    val calSeleccionado = remember(estadoUi.dtFechaSeleccionada) {
        Calendar.getInstance().apply { timeInMillis = estadoUi.dtFechaSeleccionada }
    }
    val diaSeleccionadoNum = calSeleccionado.get(Calendar.DAY_OF_MONTH)
    val mesSeleccionadoCal = calSeleccionado.get(Calendar.MONTH) + 1
    val anioSeleccionadoCal = calSeleccionado.get(Calendar.YEAR)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.agenda_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavegarNuevaCita(estadoUi.dtFechaSeleccionada) },
                modifier = Modifier.heightIn(min = 56.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_new_appointment)) },
                text = { Text(stringResource(R.string.new_appointment), fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            var nuevoMes = estadoUi.nMesSeleccionado - 1
                            var nuevoAnio = estadoUi.nAnioSeleccionado
                            if (nuevoMes < 1) {
                                nuevoMes = 12
                                nuevoAnio -= 1
                            }
                            vmCita.Calendario_ObtenerMes(nuevoMes, nuevoAnio)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_prev_month))
                    }

                    Text(
                        text = "${nombresMeses[estadoUi.nMesSeleccionado - 1]} ${estadoUi.nAnioSeleccionado}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            var nuevoMes = estadoUi.nMesSeleccionado + 1
                            var nuevoAnio = estadoUi.nAnioSeleccionado
                            if (nuevoMes > 12) {
                                nuevoMes = 1
                                nuevoAnio += 1
                            }
                            vmCita.Calendario_ObtenerMes(nuevoMes, nuevoAnio)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.cd_next_month))
                    }
                }
            }

            item {
                CuadriculaMensual(
                    nMes = estadoUi.nMesSeleccionado,
                    nAnio = estadoUi.nAnioSeleccionado,
                    diaSeleccionado = if (mesSeleccionadoCal == estadoUi.nMesSeleccionado && anioSeleccionadoCal == estadoUi.nAnioSeleccionado) diaSeleccionadoNum else -1,
                    diasConCitas = diasConCitas,
                    onSeleccionarDia = { dia ->
                        val cal = Calendar.getInstance().apply {
                            set(estadoUi.nAnioSeleccionado, estadoUi.nMesSeleccionado - 1, dia, 12, 0, 0)
                        }
                        vmCita.Cita_ObtenerPorFecha(cal.timeInMillis)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Próximas citas programadas (${estadoUi.lstCitasDelMes.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (estadoUi.lstCitasDelMes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No hay citas programadas para este mes.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(estadoUi.lstCitasDelMes) { cita ->
                    TarjetaCita(
                        objCita = cita,
                        onEliminar = { vmCita.eliminarCita(cita.lIdCita) }
                    )
                }
            }

            item {
                val formatoFecha = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "MX"))
                val fechaTexto = formatoFecha.format(Date(estadoUi.dtFechaSeleccionada))
                    .replaceFirstChar { it.uppercase() }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Eventos del día: $fechaTexto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    text = "${stringResource(R.string.medical_appointments)} (${estadoUi.lstCitasDelDia.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (estadoUi.lstCitasDelDia.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = stringResource(R.string.empty_appointments),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(estadoUi.lstCitasDelDia) { cita ->
                    TarjetaCita(
                        objCita = cita,
                        onEliminar = { vmCita.eliminarCita(cita.lIdCita) }
                    )
                }
            }

            item {
                Text(
                    text = "${stringResource(R.string.daily_care_activities)} (${estadoUi.lstActividadesActivas.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (estadoUi.lstActividadesActivas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No hay actividades registradas en la rutina.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(estadoUi.lstActividadesActivas) { actividad ->
                    TarjetaActividad(objActividad = actividad)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CuadriculaMensual(
    nMes: Int,
    nAnio: Int,
    diaSeleccionado: Int,
    diasConCitas: Set<Int>,
    onSeleccionarDia: (Int) -> Unit
) {
    val diasSemana = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
    val cal = Calendar.getInstance().apply { set(nAnio, nMes - 1, 1) }
    val primerDiaSemana = cal.get(Calendar.DAY_OF_WEEK) - 1
    val totalDiasMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val celdas = mutableListOf<Int?>()
    repeat(primerDiaSemana) { celdas.add(null) }
    for (d in 1..totalDiasMes) { celdas.add(d) }
    while (celdas.size % 7 != 0) { celdas.add(null) }

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
                    fontSize = 16.sp,
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
                        val esSeleccionado = dia == diaSeleccionado
                        val tieneCita = diasConCitas.contains(dia)

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (esSeleccionado) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .clickable { onSeleccionarDia(dia) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dia.toString(),
                                fontSize = 16.sp,
                                fontWeight = if (esSeleccionado || tieneCita) FontWeight.Bold else FontWeight.Normal,
                                color = if (esSeleccionado) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            if (tieneCita) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (esSeleccionado) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.tertiary
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaCita(objCita: CitaEntity, onEliminar: () -> Unit) {
    val formatoFechaHora = SimpleDateFormat("dd/MM - hh:mm a", Locale("es", "MX"))
    val fechaHoraTexto = formatoFechaHora.format(Date(objCita.dtFechaHora))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$fechaHoraTexto - ${objCita.strEspecialista}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ubicación: ${objCita.strUbicacion}",
                    fontSize = 16.sp
                )
                if (objCita.strNotas.isNotBlank()) {
                    Text(
                        text = "Notas: ${objCita.strNotas}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onEliminar,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete_cita),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TarjetaActividad(objActividad: ActividadResumen) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${objActividad.strHora} - ${objActividad.strNombre}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${objActividad.eTipo}: ${objActividad.strDescripcion}",
                    fontSize = 16.sp
                )
            }
        }
    }
}
