package swbg.solutions.com.maintenanceapp.dto

import swbg.solutions.com.maintenanceapp.entity.EquipmentSeasonType
import swbg.solutions.com.maintenanceapp.entity.InspectionResult
import swbg.solutions.com.maintenanceapp.entity.TaskSource
import swbg.solutions.com.maintenanceapp.entity.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class CreateManualInspectionTaskRequest(
    val equipmentId: UUID,
    val assignedEngineerId: UUID? = null,
    val plannedDate: LocalDate
)

data class ReassignInspectionTaskRequest(
    val assignedEngineerId: UUID
)

data class MoveInspectionTaskRequest(
    val plannedDate: LocalDate
)

data class InspectionTaskResponse(
    val id: UUID,
    val equipment: EquipmentSummaryResponse,
    val assignedEngineer: EngineerSummaryResponse?,
    val plannedDate: LocalDate,
    val plannedYear: Int,
    val occurrenceNumber: Int?,
    val generationKey: String?,
    val source: TaskSource,
    val status: TaskStatus,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val result: InspectionResult?,
    val inspectionReportId: UUID?
)

data class EquipmentSummaryResponse(
    val id: UUID,
    val name: String,
    val seasonType: EquipmentSeasonType,
    val zone: String
)
