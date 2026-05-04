package swbg.solutions.com.maintenanceapp.mapper

import swbg.solutions.com.maintenanceapp.dto.EngineerResponse
import swbg.solutions.com.maintenanceapp.dto.EngineerSummaryResponse
import swbg.solutions.com.maintenanceapp.entity.Engineer

fun Engineer.toSummaryResponse(): EngineerSummaryResponse {
    return EngineerSummaryResponse(
        id = requireNotNull(id),
        fullName = fullName,
        email = email
    )
}

fun Engineer.toResponse(): EngineerResponse {
    return EngineerResponse(
        id = id,
        name = fullName,
        email = email,
        active = active,
        maxTasksPerDay = maxTasksPerDay,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
