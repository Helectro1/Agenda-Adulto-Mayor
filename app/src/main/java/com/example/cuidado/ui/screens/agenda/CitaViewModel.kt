package com.example.cuidado.ui.screens.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.local.AppDatabase
import com.example.cuidado.data.local.CitaEntity
import com.example.cuidado.interfaces.datos.ActividadResumen
import com.example.cuidado.interfaces.datos.iActividadRepository
import com.example.cuidado.interfaces.datos.iCitaRepository
import com.example.cuidado.interfaces.servicios.OrigenRecordatorio
import com.example.cuidado.interfaces.servicios.iNotificador
import com.example.cuidado.interfaces.servicios.iRecordatorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class AgendaUiState(
    val nMesSeleccionado: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val nAnioSeleccionado: Int = Calendar.getInstance().get(Calendar.YEAR),
    val dtFechaSeleccionada: Long = System.currentTimeMillis(),
    val lstCitasDelMes: List<CitaEntity> = emptyList(),
    val lstCitasDelDia: List<CitaEntity> = emptyList(),
    val lstActividadesActivas: List<ActividadResumen> = emptyList(),
    val strMensajeFeedback: String? = null,
    val bCargando: Boolean = false
)

class CitaViewModel(
    private val objCitaRepository: iCitaRepository,
    private val objActividadRepository: iActividadRepository,
    private val objNotificador: iNotificador,
    private val objDatabase: AppDatabase
) : ViewModel() {

    private val _objEstadoUi = MutableStateFlow(AgendaUiState())
    val objEstadoUi: StateFlow<AgendaUiState> = _objEstadoUi.asStateFlow()

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        val estado = _objEstadoUi.value
        Calendario_ObtenerMes(estado.nMesSeleccionado, estado.nAnioSeleccionado)
        Cita_ObtenerPorFecha(estado.dtFechaSeleccionada)
    }

    fun Cita_Registrar(objCita: CitaEntity, onResultado: (Boolean) -> Unit = {}) {
        if (objCita.strEspecialista.isBlank()) {
            _objEstadoUi.update { it.copy(strMensajeFeedback = "Escribe el nombre o especialidad del médico") }
            onResultado(false)
            return
        }
        if (objCita.strUbicacion.isBlank()) {
            _objEstadoUi.update { it.copy(strMensajeFeedback = "Escribe la clínica o ubicación de la cita") }
            onResultado(false)
            return
        }

        viewModelScope.launch {
            var lNuevoId = 0L
            val bGuardado = try {
                lNuevoId = objCitaRepository.insertarCita(objCita)
                lNuevoId > 0L
            } catch (e: Exception) {
                false
            }

            if (bGuardado) {
                val objRecordatorio = iRecordatorio(
                    lIdOrigen = lNuevoId,
                    eOrigen = OrigenRecordatorio.CITA,
                    strTitulo = "Cita con ${objCita.strEspecialista} en ${objCita.strUbicacion}",
                    dtDisparo = objCita.dtFechaHora
                )
                objNotificador.Recordatorio_Programar(objRecordatorio)

                _objEstadoUi.update {
                    it.copy(strMensajeFeedback = "Cita guardada y recordatorio programado")
                }
                cargarDatosIniciales()
                onResultado(true)
            } else {
                _objEstadoUi.update {
                    it.copy(strMensajeFeedback = "No se pudo guardar la cita en la base de datos")
                }
                onResultado(false)
            }
        }
    }

    fun Cita_ObtenerPorFecha(dtFecha: Long) {
        viewModelScope.launch {
            _objEstadoUi.update { it.copy(dtFechaSeleccionada = dtFecha, bCargando = true) }
            val citasDia = objCitaRepository.obtenerPorFecha(dtFecha)
            _objEstadoUi.update {
                it.copy(lstCitasDelDia = citasDia, bCargando = false)
            }
        }
    }

    fun Calendario_ObtenerMes(nMes: Int, nAnio: Int) {
        viewModelScope.launch {
            _objEstadoUi.update {
                it.copy(nMesSeleccionado = nMes, nAnioSeleccionado = nAnio, bCargando = true)
            }
            val citasMes = objCitaRepository.obtenerPorMes(nMes, nAnio)
            val actividades = objActividadRepository.obtenerActivas()
            _objEstadoUi.update {
                it.copy(
                    lstCitasDelMes = citasMes,
                    lstActividadesActivas = actividades,
                    bCargando = false
                )
            }
        }
    }

    fun eliminarCita(lIdCita: Long) {
        viewModelScope.launch {
            val eliminada = objCitaRepository.eliminarCita(lIdCita)
            if (eliminada) {
                objNotificador.Recordatorio_Cancelar(lIdCita, OrigenRecordatorio.CITA)
                _objEstadoUi.update { it.copy(strMensajeFeedback = "Cita eliminada") }
                cargarDatosIniciales()
            }
        }
    }

    fun limpiarMensaje() {
        _objEstadoUi.update { it.copy(strMensajeFeedback = null) }
    }
}

class CitaViewModelFactory(
    private val objCitaRepository: iCitaRepository,
    private val objActividadRepository: iActividadRepository,
    private val objNotificador: iNotificador,
    private val objDatabase: AppDatabase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitaViewModel::class.java)) {
            return CitaViewModel(
                objCitaRepository,
                objActividadRepository,
                objNotificador,
                objDatabase
            ) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
