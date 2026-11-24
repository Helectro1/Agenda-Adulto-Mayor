package com.example.cuidado.ui.screens.mediciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.evaluator.EvaluadorAlertas
import com.example.cuidado.data.evaluator.AlertaMedicion
import com.example.cuidado.data.model.Medicion
import com.example.cuidado.data.model.RangoMedicion
import com.example.cuidado.data.repository.MedicionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedicionViewModel : ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val evaluador = EvaluadorAlertas()

    private val tipos = listOf("Glucosa", "PresionSistolica", "PresionDiastolica", "Peso", "Temperatura")

    val mediciones: StateFlow<List<Medicion>> = MedicionRepository.getMediciones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _rangosState = MutableStateFlow<Map<String, RangoMedicion>>(emptyMap())
    val rangosState: StateFlow<Map<String, RangoMedicion>> = _rangosState.asStateFlow()

    init {
        viewModelScope.launch {
            cargarRangos()
        }
    }

    suspend fun cargarRangos() {
        val map = mutableMapOf<String, RangoMedicion>()
        for (tipo in tipos) {
            map[tipo] = MedicionRepository.getRango(tipo)
        }
        _rangosState.value = map
    }

    suspend fun obtenerRango(tipo: String): RangoMedicion {
        return MedicionRepository.getRango(tipo)
    }

    val alertasActivas: StateFlow<List<AlertaMedicion>> = combine(
        mediciones,
        _rangosState
    ) { meds, rangos ->
        tipos.map { tipo ->
            val rango = rangos[tipo] ?: run {
                when (tipo.lowercase()) {
                    "glucosa" -> RangoMedicion(tipo, 70.0, 140.0, 3)
                    "presionsistolica" -> RangoMedicion(tipo, 90.0, 130.0, 3)
                    "presiondiastolica" -> RangoMedicion(tipo, 60.0, 85.0, 3)
                    "peso" -> RangoMedicion(tipo, 40.0, 120.0, 5)
                    "temperatura" -> RangoMedicion(tipo, 36.0, 37.5, 2)
                    else -> RangoMedicion(tipo, 0.0, 100.0, 3)
                }
            }
            evaluador.evaluarConsecutivos(meds, tipo, rango.valorMin, rango.valorMax, rango.umbralConsecutivo)
        }.filter { it.isAlertActive }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun evaluarAlertaParaTipo(tipo: String): AlertaMedicion {
        val lista = mediciones.value
        val rango = MedicionRepository.getRango(tipo)
        return evaluador.evaluarConsecutivos(lista, tipo, rango.valorMin, rango.valorMax, rango.umbralConsecutivo)
    }

    fun guardarConfiguracionRango(
        tipo: String,
        minStr: String,
        maxStr: String,
        umbralStr: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        val min = minStr.toDoubleOrNull()
        val max = maxStr.toDoubleOrNull()
        val umbral = umbralStr.toIntOrNull()
        if (min == null || max == null || umbral == null) {
            onError("Introduce valores numéricos válidos")
            return
        }
        if (min >= max) {
            onError("El valor mínimo debe ser menor que el valor máximo")
            return
        }
        if (umbral < 1) {
            onError("El umbral N debe ser al menos 1")
            return
        }
        viewModelScope.launch {
            val rango = RangoMedicion(
                tipo = tipo,
                valorMin = min,
                valorMax = max,
                umbralConsecutivo = umbral
            )
            MedicionRepository.saveRango(rango)
            cargarRangos()
            onSaved()
        }
    }

    fun saveMedicion(
        tipo: String,
        valorStr: String,
        notas: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        val valor = valorStr.toDoubleOrNull()
        if (valor == null) {
            onError("Introduce un valor numérico válido")
            return
        }
        viewModelScope.launch {
            val medicion = Medicion(
                tipo = tipo,
                valor = valor,
                fecha = dateFormatter.format(Date()),
                hora = timeFormatter.format(Date()),
                notas = notas.trim()
            )
            MedicionRepository.saveMedicion(medicion)
            onSaved()
        }
    }
}
