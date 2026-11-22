package com.example.cuidado.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Caregiver
import com.example.cuidado.data.model.Profile
import com.example.cuidado.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Lógica de la pantalla de Perfil: adulto mayor y cuidador principal.
 * No depende de la interfaz, así se puede probar por separado.
 */
class ProfileViewModel : ViewModel() {

    val profile: StateFlow<Profile> = ProfileRepository.getProfile()
        .map { it ?: Profile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Profile())

    val caregiver: StateFlow<Caregiver> = ProfileRepository.getCaregiver()
        .map { it ?: Caregiver() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Caregiver())

    /**
     * Guarda el perfil del adulto mayor. Llama a onSaved solo después de
     * que Room confirmó el guardado, y a onError si los datos no son válidos.
     */
    fun saveProfile(
        name: String,
        age: Int,
        conditions: String,
        frequentMedications: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onError("El nombre es obligatorio")
            return
        }
        if (age !in 0..120) {
            onError("La edad debe estar entre 0 y 120")
            return
        }
        viewModelScope.launch {
            ProfileRepository.saveProfile(
                Profile(name.trim(), age, conditions.trim(), frequentMedications.trim())
            )
            onSaved()
        }
    }

    /**
     * Guarda los datos del cuidador principal, con la misma regla de confirmación.
     */
    fun saveCaregiver(
        name: String,
        phone: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onError("El nombre es obligatorio")
            return
        }
        viewModelScope.launch {
            ProfileRepository.saveCaregiver(Caregiver(name.trim(), phone.trim()))
            onSaved()
        }
    }
}