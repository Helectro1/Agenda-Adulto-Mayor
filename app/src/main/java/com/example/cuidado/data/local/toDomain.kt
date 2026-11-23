package com.example.cuidado.data.local

import com.example.cuidado.data.model.Caregiver
import com.example.cuidado.data.model.Profile

fun ProfileEntity.toDomain() = Profile(name, age, conditions, frequentMedications)
fun Profile.toEntity() = ProfileEntity(1, name, age, conditions, frequentMedications)

fun CaregiverEntity.toDomain() = Caregiver(name, phone)
fun Caregiver.toEntity() = CaregiverEntity(1, name, phone)