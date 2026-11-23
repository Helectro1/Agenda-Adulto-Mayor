package com.example.cuidado.ui.screens.agenda

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuidado.R
import com.example.cuidado.data.local.CitaEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaFormScreen(
    vmCita: CitaViewModel,
    dtFechaInicial: Long,
    onRegresar: () -> Unit
) {
    val context = LocalContext.current
    val estadoUi by vmCita.objEstadoUi.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estadoUi.strMensajeFeedback) {
        estadoUi.strMensajeFeedback?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            vmCita.limpiarMensaje()
        }
    }

    val calendario = remember {
        Calendar.getInstance().apply {
            timeInMillis = dtFechaInicial
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    var dtFechaHoraSeleccionada by remember { mutableLongStateOf(calendario.timeInMillis) }
    var strEspecialista by remember { mutableStateOf("") }
    var strUbicacion by remember { mutableStateOf("") }
    var strNotas by remember { mutableStateOf("") }

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
    val formatoHora = SimpleDateFormat("hh:mm a", Locale("es", "MX"))

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.register_appointment_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onRegresar,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_go_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = dtFechaHoraSeleccionada }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            cal.set(Calendar.YEAR, year)
                            cal.set(Calendar.MONTH, month)
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            dtFechaHoraSeleccionada = cal.timeInMillis
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${stringResource(R.string.date_label)} ${formatoFecha.format(Date(dtFechaHoraSeleccionada))}",
                    fontSize = 18.sp
                )
            }

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = dtFechaHoraSeleccionada }
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal.set(Calendar.MINUTE, minute)
                            cal.set(Calendar.SECOND, 0)
                            dtFechaHoraSeleccionada = cal.timeInMillis
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${stringResource(R.string.time_label)} ${formatoHora.format(Date(dtFechaHoraSeleccionada))}",
                    fontSize = 18.sp
                )
            }

            OutlinedTextField(
                value = strEspecialista,
                onValueChange = { strEspecialista = it },
                label = { Text(stringResource(R.string.specialist_label), fontSize = 16.sp) },
                placeholder = { Text(stringResource(R.string.specialist_placeholder), fontSize = 16.sp) },
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = strUbicacion,
                onValueChange = { strUbicacion = it },
                label = { Text(stringResource(R.string.location_label), fontSize = 16.sp) },
                placeholder = { Text(stringResource(R.string.location_placeholder), fontSize = 16.sp) },
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = strNotas,
                onValueChange = { strNotas = it },
                label = { Text(stringResource(R.string.notes_label), fontSize = 16.sp) },
                placeholder = { Text(stringResource(R.string.notes_placeholder), fontSize = 16.sp) },
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val nuevaCita = CitaEntity(
                        dtFechaHora = dtFechaHoraSeleccionada,
                        strEspecialista = strEspecialista.trim(),
                        strUbicacion = strUbicacion.trim(),
                        strNotas = strNotas.trim()
                    )
                    vmCita.Cita_Registrar(nuevaCita) { exito ->
                        if (exito) {
                            onRegresar()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_appointment_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
