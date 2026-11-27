package com.example.cuidado.ui.screens.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
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

    val categoriaFiltro = estadoUi.categoriaSeleccionada
    val citasDelMesFiltradas = remember(estadoUi.lstCitasDelMes, categoriaFiltro) {
        if (categoriaFiltro.isNullOrBlank() || categoriaFiltro == "Todas") {
            estadoUi.lstCitasDelMes
        } else {
            estadoUi.lstCitasDelMes.filter { it.strCategoria.equals(categoriaFiltro, ignoreCase = true) }
        }
    }

    val citasDelDiaFiltradas = remember(estadoUi.lstCitasDelDia, categoriaFiltro) {
        if (categoriaFiltro.isNullOrBlank() || categoriaFiltro == "Todas") {
            estadoUi.lstCitasDelDia
        } else {
            estadoUi.lstCitasDelDia.filter { it.strCategoria.equals(categoriaFiltro, ignoreCase = true) }
        }
    }

    val diasConCitas = remember(citasDelMesFiltradas) {
        citasDelMesFiltradas.map { cita ->
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
            // Encabezado de navegación mensual y cuadrícula de calendario
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

            // Fila horizontal (LazyRow) de FilterChip por categoría
            item {
                Spacer(modifier = Modifier.height(4.dp))
                val categorias = listOf("Todas") + estadoUi.categoriasDisponibles
                val categoriaSeleccionada = estadoUi.categoriaSeleccionada ?: "Todas"

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorias) { cat ->
                        FilterChip(
                            selected = categoriaSeleccionada.equals(cat, ignoreCase = true) || (cat == "Todas" && estadoUi.categoriaSeleccionada == null),
                            onClick = { vmCita.seleccionarCategoria(cat) },
                            label = { Text(cat) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                    }
                }
            }

            // Sección Citas del día seleccionado
            item {
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
                val fechaTexto = formatoFecha.format(Date(estadoUi.dtFechaSeleccionada))

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Citas del $fechaTexto (${citasDelDiaFiltradas.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (citasDelDiaFiltradas.isEmpty()) {
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
                items(citasDelDiaFiltradas) { cita ->
                    val ahora = System.currentTimeMillis()
                    val esPasada = cita.dtFechaHora < ahora
                    var showEditNotesDialog by remember { mutableStateOf(false) }

                    TarjetaCita(
                        objCita = cita,
                        onEliminar = { vmCita.eliminarCita(cita.lIdCita) },
                        onClick = if (esPasada) { { showEditNotesDialog = true } } else null
                    )

                    if (showEditNotesDialog) {
                        EditarNotasCitaDialog(
                            cita = cita,
                            onDismiss = { showEditNotesDialog = false },
                            onSave = { nuevasNotas ->
                                vmCita.Cita_ActualizarNotas(cita, nuevasNotas)
                                showEditNotesDialog = false
                            }
                        )
                    }
                }
            }

            // Sección Próximas Citas
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Próximas citas (${estadoUi.proximasCitas.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (estadoUi.proximasCitas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No hay próximas citas programadas.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(estadoUi.proximasCitas) { cita ->
                    TarjetaCita(
                        objCita = cita,
                        onEliminar = { vmCita.eliminarCita(cita.lIdCita) },
                        onClick = null
                    )
                }
            }

            // Sección Citas Anteriores
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Citas anteriores (${estadoUi.citasAnteriores.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Toca cualquier cita pasada para ver o editar sus notas importantes.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (estadoUi.citasAnteriores.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No hay citas anteriores registradas.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(estadoUi.citasAnteriores) { cita ->
                    var showEditNotesDialog by remember { mutableStateOf(false) }
                    TarjetaCita(
                        objCita = cita,
                        onEliminar = { vmCita.eliminarCita(cita.lIdCita) },
                        onClick = { showEditNotesDialog = true }
                    )

                    if (showEditNotesDialog) {
                        EditarNotasCitaDialog(
                            cita = cita,
                            onDismiss = { showEditNotesDialog = false },
                            onSave = { nuevasNotas ->
                                vmCita.Cita_ActualizarNotas(cita, nuevasNotas)
                                showEditNotesDialog = false
                            }
                        )
                    }
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
private fun TarjetaCita(
    objCita: CitaEntity,
    onEliminar: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    val formatoFechaHora = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale("es", "MX"))
    val fechaHoraTexto = formatoFechaHora.format(Date(objCita.dtFechaHora))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = objCita.strEspecialista,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = objCita.strCategoria,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = fechaHoraTexto,
                    fontSize = 16.sp
                )
                Text(
                    text = "Ubicación: ${objCita.strUbicacion}",
                    fontSize = 16.sp
                )
                if (objCita.strNotas.isNotBlank()) {
                    Text(
                        text = "Notas: ${objCita.strNotas}",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
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
private fun EditarNotasCitaDialog(
    cita: CitaEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var notas by remember { mutableStateOf(cita.strNotas) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notas de la consulta (${cita.strEspecialista})", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Edita las indicaciones, comentarios del especialista o acuerdos importantes:", fontSize = 14.sp)
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas importantes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(notas) }
            ) {
                Text("Guardar notas")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
