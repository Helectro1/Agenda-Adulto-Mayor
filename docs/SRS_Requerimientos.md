# Especificación de Requerimientos de Software (SRS) 

**Proyecto:**   
**Plataforma:** Android (Kotlin, Jetpack Compose, Room Database)  
**Materia:** Ingeniería de Software  

---

## 1. Requerimientos Funcionales (RF)

| Id | Tipo | Funcionalidad | Requerimiento | Prioridad | Comentarios | Id_Test_Case_Satisfied |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **RF-001** | Funcional | Registro  | El sistema deberá permitir al cuidador crear una nueva actividad indicando nombre, descripción, horario y tipo (ej. Medicamento, Cuidado, General). | Alta | Módulo de Registro. Entidad base de programación. | TC-MOD-001, TC-INT-001, TC-SYS-001 |
| **RF-002** | Funcional | Registro  | El sistema deberá permitir al usuario visualizar la lista de actividades programadas para el día actual. | Alta | Pantalla principal (HomeScreen / ActivitiesScreen). | TC-MOD-002, TC-INT-001, TC-SYS-001 |
| **RF-003** | Funcional | Registro  | El sistema deberá permitir marcar una actividad como realizada, pendiente u omitida. | Alta | Genera el registro en el historial (`ActivityLog`). | TC-MOD-003, TC-INT-002, TC-SYS-002 |
| **RF-004** | Funcional | Registro  | El sistema deberá permitir agregar observaciones de texto al momento de registrar la ejecución de una actividad. | Media | Útil para notas de síntomas o aclaraciones del cuidador. | TC-MOD-004, TC-SYS-002 |
| **RF-005** | Funcional | Registro  | El sistema deberá permitir la edición y eliminación de actividades previamente programadas. | Media | Modificación de la definición base de la actividad. | TC-MOD-005, TC-INT-003 |
| **RF-006** | Funcional | Agenda  | El sistema deberá permitir al cuidador registrar citas médicas con fecha, hora, especialista y ubicación. | Alta | Módulo de Agenda. | TC-MOD-006, TC-INT-004, TC-SYS-003 |
| **RF-007** | Funcional | Agenda  | El sistema deberá mostrar una vista de calendario/agenda con los eventos y citas programadas. | Alta | Organización visual del calendario. | TC-MOD-007, TC-INT-004 |
| **RF-008** | Funcional | Agenda  | El sistema deberá generar recordatorios y notificaciones locales para las actividades y citas agendadas. | Alta | Uso de `AlarmManager` / `WorkManager` de Android. | TC-MOD-008, TC-SYS-004 |
| **RF-009** | Funcional | Perfil  | El sistema deberá permitir el registro y edición de la información general de la persona dependiente (nombre, edad, notas médicas). | Alta | Módulo Perfil. | TC-MOD-009, TC-INT-005, TC-SYS-005 |
| **RF-010** | Funcional | Perfil  | El sistema deberá guardar la información de contacto y preferencias del cuidador principal. | Media | Configuración general de la app. | TC-MOD-010, TC-INT-005 |
| **RF-011** | Funcional | Persistencia (Core) | El sistema deberá conservar todas las actividades, citas e historial almacenados en base de datos local tras cerrar y reabrir la app. | Alta | Persistencia mediante Room Database / SQLite. | TC-INT-003, TC-SYS-001, TC-SYS-005 |
| **RF-012** | Funcional | Historial (Core) | El sistema deberá permitir consultar el historial de actividades realizadas u omitidas en fechas anteriores. | Alta | Base para el posterior análisis estadístico. | TC-MOD-003, TC-SYS-002 |

---

## 2. Requerimientos No Funcionales (RNF)

| Id | Tipo | Funcionalidad | Requerimiento | Prioridad | Comentarios | Id_Test_Case_Satisfied |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **RNF-001** | No Funcional | Usabilidad | La interfaz de usuario deberá ser clara, legible y accesible para uso rápido en situaciones de cuidado diario. | Alta | Diseño simplificado en Jetpack Compose. | TC-SYS-001, TC-SYS-005 |
| **RNF-002** | No Funcional | Rendimiento | Las consultas a la base de datos local y el renderizado de pantallas no deberán demorar más de 2 segundos. | Alta | Uso de Kotlin Coroutines y Flow para operaciones asíncronas. | TC-INT-002, TC-INT-003 |
| **RNF-003** | No Funcional | Privacidad y Seguridad | Los datos personales y de salud deberán almacenarse exclusivamente de forma local en el dispositivo del usuario. | Alta | Sin envío de datos sensibles a servidores externos. | TC-SYS-005 |
| **RNF-004** | No Funcional | Arquitectura | El código del sistema deberá seguir el patrón MVVM y Repository para garantizar la separación de responsabilidades y mantenibilidad. | Alta | Guía de arquitectura oficial para Android. | TC-MOD-001, TC-INT-001 |

---

## 3. Nomenclatura de Pruebas Asociadas
- **`TC-MOD-xxx`**: Casos de prueba Modulares / Unitarios (DAOs, Mappers, ViewModels aislados).
- **`TC-INT-xxx`**: Casos de prueba de Integración (Conexión UI + ViewModel + Repository + Room).
- **`TC-SYS-xxx`**: Casos de prueba de Sistema / E2E (Flujos completos del usuario).
