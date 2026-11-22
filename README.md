# Cuida — Asistente Integral para el Cuidado del Adulto Mayor

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin_2.2-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Database](https://img.shields.io/badge/Persistence-Room_v4-009688)
![Architecture](https://img.shields.io/badge/Architecture-MVVM_%2B_Repository-orange)

**Cuida** es una aplicación móvil nativa para Android diseñada para reducir la carga mental y operativa de los cuidadores primarios de adultos mayores. Permite organizar rutinas diarias, registrar el cumplimiento de actividades con observaciones, dar seguimiento a mediciones preventivas y gestionar citas médicas con recordatorios locales.

> **Aviso de alcance:** *Cuida* es una herramienta de organización, seguimiento y apoyo al cuidador. No constituye un dispositivo médico ni emite diagnósticos clínicos.

---

## Tabla de Contenidos

1. [Problema y Propuesta de Valor](#1-problema-y-propuesta-de-valor)
2. [Arquitectura y Stack Tecnológico](#2-arquitectura-y-stack-tecnológico)
3. [Estructura del Repositorio](#3-estructura-del-repositorio)
4. [Módulos Funcionales y Estado](#4-módulos-funcionales-y-estado)
5. [Esquema de Base de Datos](#5-esquema-de-base-de-datos)
6. [Estrategia de Control de Versiones](#6-estrategia-de-control-de-versiones)
7. [Instalación y Ejecución](#7-instalación-y-ejecución)
8. [Equipo de Desarrollo y Roles](#8-equipo-de-desarrollo-y-roles)

---

## 1. Problema y Propuesta de Valor

El cuidado cotidiano de un adulto mayor exige coordinar horarios de medicación, alimentación, higiene, consultas médicas y monitoreo de indicadores básicos. Cuando esta gestión depende de la memoria o de anotaciones dispersas, aumentan los olvidos y el desgaste del cuidador (*burnout*).

**Cuida** resuelve este problema mediante:
* **Centralización offline-first:** Toda la información crítica reside localmente en el dispositivo sin depender de conexión a internet.
* **Accesibilidad enfocada al usuario (RNF-004):** Interfaz con tipografía mínima de `16sp`, alto contraste y áreas táctiles de al menos `48dp`.
* **Detección preventiva de patrones:** Evaluación determinista de rachas consecutivas fuera de rango en mediciones para alertar oportunamente al cuidador.

---

## 2. Arquitectura y Stack Tecnológico

El sistema implementa una arquitectura en capas basada en **MVVM (Model-View-ViewModel) + Repository**, garantizando el desacoplamiento entre la interfaz declarativa, la lógica de negocio y la persistencia local.

* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose + Material Design 3 + Navigation Compose
* **Persistencia Local:** Room Database (SQLite) con migraciones incrementales
* **Asincronía y Estado:** Kotlin Coroutines + `StateFlow`
* **Pruebas:** JUnit 4 (pruebas unitarias de lógica pura y mappers)

```text
┌──────────────────────────────────────────────────────────┐
│                 UI Layer (Jetpack Compose)               │
│  HomeScreen · ActivitiesScreen · Mediciones · Profile    │
└────────────────────────────┬─────────────────────────────┘
                             │ StateFlow / Eventos
┌────────────────────────────▼─────────────────────────────┐
│                  Presentation (ViewModels)               │
│  HomeViewModel · ActivitiesVM · MedicionVM · ProfileVM   │
└────────────────────────────┬─────────────────────────────┘
                             │ Dominio
┌────────────────────────────▼─────────────────────────────┐
│             Domain & Repositories / Evaluators           │
│  ActivityRepo · ProfileRepo · MedicionRepo · Evaluador   │
└────────────────────────────┬─────────────────────────────┘
                             │ Entidades / DAOs
┌────────────────────────────▼─────────────────────────────┐
│               Data Layer (Room Database v4)              │
│   activities · activity_logs · profile · mediciones      │
└──────────────────────────────────────────────────────────┘
