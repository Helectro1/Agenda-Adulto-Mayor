package com.example.cuidado.data.local

import com.example.cuidado.data.model.Activity
import com.example.cuidado.data.model.ActivityLog

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        id = id,
        name = name,
        description = description,
        time = time,
        type = type,
        repeatDays = repeatDays,
        isActive = isActive,
        color = color
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        name = name,
        description = description,
        time = time,
        type = type,
        repeatDays = repeatDays,
        isActive = isActive,
        color = color
    )
}

fun ActivityLogEntity.toDomain(): ActivityLog {
    return ActivityLog(
        id = id,
        activityId = activityId,
        date = date,
        status = status,
        executionTime = executionTime
    )
}

fun ActivityLog.toEntity(): ActivityLogEntity {
    return ActivityLogEntity(
        id = id,
        activityId = activityId,
        date = date,
        status = status,
        executionTime = executionTime
    )
}
