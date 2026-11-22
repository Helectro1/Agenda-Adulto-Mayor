package com.example.cuidado.data.evaluator

import com.example.cuidado.data.model.Medicion

data class AlertaMedicion(
    val tipo: String,
    val mensaje: String,
    val isAlertActive: Boolean
)

class EvaluadorAlertas {

    /**
     * Evalúa si las últimas [umbralN] mediciones consecutivas del [tipo] especificado
     * se encuentran fuera del rango [valorMin] y [valorMax].
     * (Estrictamente informativo, sin emitir diagnósticos médicos).
     */
    fun evaluarConsecutivos(
        mediciones: List<Medicion>,
        tipo: String,
        valorMin: Double,
        valorMax: Double,
        umbralN: Int
    ): AlertaMedicion {
        val filtradas = mediciones.filter { it.tipo.equals(tipo, ignoreCase = true) }
        if (filtradas.isEmpty() || umbralN <= 0) {
            return AlertaMedicion(tipo, "Sin registros suficientes para evaluar alertas.", false)
        }
        val ultimas = filtradas.take(umbralN)
        if (ultimas.size < umbralN) {
            return AlertaMedicion(tipo, "Registros insuficientes (se requieren $umbralN, hay ${ultimas.size}).", false)
        }

        val fueraDeRango = ultimas.all { it.valor < valorMin || it.valor > valorMax }
        return if (fueraDeRango) {
            AlertaMedicion(
                tipo = tipo,
                mensaje = "Atención: Las últimas $umbralN mediciones de $tipo están fuera del rango normal ($valorMin - $valorMax).",
                isAlertActive = true
            )
        } else {
            AlertaMedicion(tipo, "Valores estables para $tipo.", false)
        }
    }
}
