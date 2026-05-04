package swbg.solutions.com.maintenanceapp.mapper

import swbg.solutions.com.maintenanceapp.dto.InspectionTaskResponse
import swbg.solutions.com.maintenanceapp.dto.InspectionTaskSummaryResponse
import swbg.solutions.com.maintenanceapp.entity.InspectionResult
import swbg.solutions.com.maintenanceapp.entity.InspectionTask
import java.util.UUID

fun InspectionTask.toSummaryResponse(): InspectionTaskSummaryResponse {
    return InspectionTaskSummaryResponse(
        id = requireNotNull(id),
        equipment = equipment.toSummaryResponse(),
        plannedDate = plannedDate,
        status = status
    )
}

fun InspectionTask.toResponse(
    result: InspectionResult? = null,
    inspectionReportId: UUID? = null
): InspectionTaskResponse {
    return InspectionTaskResponse(
        id = requireNotNull(id),
        equipment = equipment.toSummaryResponse(),
        assignedEngineer = assignedEngineer?.toSummaryResponse(),
        plannedDate = plannedDate,
        plannedYear = plannedYear,
        occurrenceNumber = occurrenceNumber,
        generationKey = generationKey,
        source = source,
        status = status,
        result = result,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        inspectionReportId = inspectionReportId
    )
}
