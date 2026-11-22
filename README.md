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

## 3. Estructura del Repositorio

Siguiendo prácticas de **Gestión de Configuración de Software (SCM)**, el repositorio centraliza tanto el código fuente como los artefactos de ingeniería vigentes dentro de la carpeta [`docs/`](https://www.google.com/search?q=docs/):

- `README.md`: Documentación general y guía de entrada del proyecto.
- `docs/`: *SW Project Folder* con especificaciones y control de calidad (`01-SRS-Requisitos.md`, `02-DAS-Arquitectura.md`, `03-SDD-Diseno-Detallado.md`, `04-Plan-y-Matriz-Pruebas.md` y `pdfs/`).
- `app/src/main/java/com/example/cuidado/data/`:
  - `evaluator/`: Lógica pura de evaluación preventiva (`EvaluadorAlertas`).
  - `local/`: Entidades Room, DAOs, Mappers y `AppDatabase`.
  - `model/`: Modelos de dominio (`Activity`, `Profile`, `Medicion`, etc.).
  - `repository/`: Repositorios centralizados (`ActivityRepository`, `ProfileRepository`, `MedicionRepository`).
- `app/src/main/java/com/example/cuidado/ui/`:
  - `components/`: Componentes reutilizables (`ActivityCard`, `BottomNavigationBar`).
  - `navigation/`: Grafo de navegación (`NavGraph`).
  - `screens/`: Pantallas y ViewModels por módulo (`home`, `activities`, `mediciones`, `profile`).
  - `theme/`: Tipografía, paleta de colores y tema Material 3.
- `app/src/test/java/com/example/cuidado/`: Pruebas unitarias en JUnit.

## 4. Módulos Funcionales y Estado

| **Módulo** | **Requerimientos** | **Responsable** | **Estado** |
|---|---|---|---|
| **Perfil del Adulto Mayor y Cuidador** | RF-009, RF-010, RF-011 | Nicolás | ✅ Integrado |
| **Gestión de Actividades y Rutina Diaria** | RF-001 a RF-005, RF-018 | Héctor | ✅ Integrado |
| **Mediciones y Alertas Preventivas** | RF-013, RF-014, RF-015 | Héctor | ✅ Integrado |
| **Agenda de Citas y Notificaciones** | RF-006, RF-007, RF-008, RF-012 | David | 🔄 En desarrollo |
| **Aseguramiento de Calidad (QA)** | TC-MOD, TC-INT, TC-SYS | Jimena | 🔄 En ejecución |

## 5. Esquema de Base de Datos

La base de datos `cuidado_database` (Room) se encuentra actualmente en la **versión 4**, con rutas de migración explícitas (`MIGRATION_1_2`, `MIGRATION_2_3`, `MIGRATION_3_4`) para preservar los datos del usuario ante actualizaciones:

- **`activities`:** Catálogo de actividades programadas (nombre, hora, días de repetición, categoría).
- **`activity_logs`:** Bitácora diaria de cumplimiento (`COMPLETED`, `SKIPPED`), hora real y observaciones (`notes`).
- **`profile` y `caregiver`:** Datos demográficos, padecimientos, medicamentos frecuentes y contacto de emergencia.
- **`mediciones` y `rangos_medicion`:** Registro histórico de variables (Glucosa, Presión Sistólica/Diastólica, Peso, Temperatura) y umbrales configurables de alerta consecutiva.

## 6. Estrategia de Control de Versiones

El desarrollo se rige por un flujo de ramas controlado (**Git Flow**) para proteger la estabilidad del código:

- **`main`:** Código estable listo para liberación y demostración.
- **`develop`:** Rama de integración continua donde convergen los módulos aprobados.
- **`feature/<modulo>`:** Ramas aisladas por funcionalidad (`feature/perfil`, `feature/registro`, `feature/agenda`, `feature/pruebas`), integradas a `develop` mediante *Pull Requests* revisados por el *SW Team Leader*.

## 7. Instalación y Ejecución

1. **Clonar el repositorio:** Ejecuta `git clone https://github.com/Helectro1/Agenda-Adulto-Mayor.git` y entra a la carpeta del proyecto.
2. **Abrir en Android Studio:** Selecciona *File > Open*, elige la carpeta raíz y espera a que Gradle sincronice (JDK 17+, Kotlin 2.2, Compile SDK 35).
3. **Compilar APK de depuración:** Ejecuta `./gradlew assembleDebug` en la terminal.
4. **Ejecutar pruebas unitarias:** Ejecuta `./gradlew testDebugUnitTest` para correr la suite JUnit.

## 8. Equipo de Desarrollo y Roles

| **Integrante** | **Rol en el Proyecto** | **Responsabilidad Principal** |
|---|---|---|
| **Luis** | SW Team Leader (SWTL) / Arquitectura | Coordinación técnica, integración en `develop`/`main` y trazabilidad SRS/DAS. |
| **Nicolás** | Desarrollador — Módulo Perfil | Gestión de datos del adulto mayor, cuidador y persistencia en Room. |
| **Héctor** | Desarrollador — Módulo Registro | CRUD de actividades, bitácora con observaciones, mediciones y evaluador de alertas. |
| **David** | Desarrollador — Módulo Agenda | Citas médicas, vista de calendario, historial y notificaciones locales. |
| **Jimena** | QA / Control de Calidad | Diseño y ejecución de pruebas unitarias (JUnit), integración y sistema. |
