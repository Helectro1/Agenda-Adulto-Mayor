package com.example.cuidado.ui.screens.mediciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.evaluator.EvaluadorAlertas
import com.example.cuidado.data.evaluator.AlertaMedicion
import com.example.cuidado.data.model.Medicion
import com.example.cuidado.data.repository.MedicionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedicionViewModel : ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val evaluador = EvaluadorAlertas()

    val mediciones: StateFlow<List<Medicion>> = MedicionRepository.getMediciones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun evaluarAlertaParaTipo(tipo: String): AlertaMedicion {
        val lista = mediciones.value
        val rango = MedicionRepository.getRango(tipo)
        return evaluador.evaluarConsecutivos(lista, tipo, rango.valorMin, rango.valorMax, rango.umbralConsecutivo)
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
