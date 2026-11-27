package com.example.cuidado.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.model.ActivityLog
import com.example.cuidado.data.repository.ActivityRepository
import com.example.cuidado.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class DayStatus {
    FUTURE,
    OUT_OF_RANGE,
    UNSTARTED,
    PARCIAL,
    COMPLETE
}

data class DayProgress(
    val day: Int,
    val dateStr: String,
    val status: DayStatus,
    val progress: Float,
    val totalActivities: Int,
    val completedActivities: Int
)

data class DayActivityDetail(
    val activity: Activity,
    val status: String,
    val executionTime: String?,
    val notes: String?
)

data class HistoryUiState(
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1, // 1-12
    val selectedDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    val daysProgress: List<DayProgress> = emptyList(),
    val dayDetails: List<DayActivityDetail> = emptyList(),
    val isBeforeStart: Boolean = false
)

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayStr = dateFormat.format(Date())

    private val activitiesFlow = ActivityRepository.getActivities()
    private val logsFlow = ActivityRepository.getAllLogs()

    init {
        viewModelScope.launch {
            combine(activitiesFlow, logsFlow, _uiState) { activities, logs, state ->
                calculateMonthProgress(activities, logs, state.year, state.month, state.selectedDay)
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun changeMonth(year: Int, month: Int) {
        val maxDays = getDaysInMonth(year, month)
        val newDay = if (_uiState.value.selectedDay > maxDays) maxDays else _uiState.value.selectedDay
        _uiState.value = _uiState.value.copy(year = year, month = month, selectedDay = newDay)
    }

    fun selectDay(day: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
    }

    private fun calculateMonthProgress(
        activities: List<Activity>,
        logs: List<ActivityLog>,
        year: Int,
        month: Int,
        selectedDay: Int
    ): HistoryUiState {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val profileStartDate = ProfileRepository.getProfileStartDate()
        val earliestLogDate = logs.minOfOrNull { it.date }
        val effectiveStartDateStr = if (earliestLogDate != null && earliestLogDate < profileStartDate) {
            earliestLogDate
        } else {
            profileStartDate
        }

        val daysProgress = mutableListOf<DayProgress>()
        val logsMap = logs.groupBy { it.date }

        for (d in 1..maxDays) {
            cal.set(year, month - 1, d)
            val dateStr = dateFormat.format(cal.time)
            val isOutOfRange = dateStr < effectiveStartDateStr || dateStr > todayStr

            if (isOutOfRange) {
                val status = if (dateStr > todayStr) DayStatus.FUTURE else DayStatus.OUT_OF_RANGE
                daysProgress.add(DayProgress(d, dateStr, status, 0f, 0, 0))
                continue
            }

            val dowJava = cal.get(Calendar.DAY_OF_WEEK)
            val dowCustom = when (dowJava) {
                Calendar.MONDAY -> "1"
                Calendar.TUESDAY -> "2"
                Calendar.WEDNESDAY -> "3"
                Calendar.THURSDAY -> "4"
                Calendar.FRIDAY -> "5"
                Calendar.SATURDAY -> "6"
                Calendar.SUNDAY -> "7"
                else -> "1"
            }

            val dayActivities = activities.filter { act ->
                act.isActive && (act.time.startsWith("EVERY_") || act.repeatDays.contains(dowCustom))
            }

            val total = dayActivities.size
            if (total == 0) {
                daysProgress.add(DayProgress(d, dateStr, DayStatus.UNSTARTED, 0f, 0, 0))
                continue
            }

            val dayLogs = logsMap[dateStr] ?: emptyList()
            val completedCount = dayActivities.count { act ->
                dayLogs.any { it.activityId == act.id && it.status == "COMPLETED" }
            }

            val progress = completedCount.toFloat() / total.toFloat()
            val status = when {
                progress >= 1.0f -> DayStatus.COMPLETE
                progress > 0.0f -> DayStatus.PARCIAL
                else -> DayStatus.UNSTARTED
            }

            daysProgress.add(DayProgress(d, dateStr, status, progress, total, completedCount))
        }

        cal.set(year, month - 1, selectedDay)
        val selectedDateStr = dateFormat.format(cal.time)
        val isBeforeStart = selectedDateStr < effectiveStartDateStr

        val dayDetails = if (isBeforeStart) {
            emptyList()
        } else {
            val dowJava = cal.get(Calendar.DAY_OF_WEEK)
            val dowCustom = when (dowJava) {
                Calendar.MONDAY -> "1"
                Calendar.TUESDAY -> "2"
                Calendar.WEDNESDAY -> "3"
                Calendar.THURSDAY -> "4"
                Calendar.FRIDAY -> "5"
                Calendar.SATURDAY -> "6"
                Calendar.SUNDAY -> "7"
                else -> "1"
            }

            val selectedDayActivities = activities.filter { act ->
                act.isActive && (act.time.startsWith("EVERY_") || act.repeatDays.contains(dowCustom))
            }

            val selectedDayLogs = logs.filter { it.date == selectedDateStr }
            selectedDayActivities.map { act ->
                val log = selectedDayLogs.find { it.activityId == act.id }
                DayActivityDetail(
                    activity = act,
                    status = log?.status ?: "Sin registro",
                    executionTime = log?.executionTime,
                    notes = log?.notes
                )
            }
        }

        return _uiState.value.copy(
            daysProgress = daysProgress,
            dayDetails = dayDetails,
            isBeforeStart = isBeforeStart
        )
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}
