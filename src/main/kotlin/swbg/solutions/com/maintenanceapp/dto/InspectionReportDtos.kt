package swbg.solutions.com.maintenanceapp.dto

import swbg.solutions.com.maintenanceapp.entity.InspectionReportStatus
import swbg.solutions.com.maintenanceapp.entity.InspectionReportType
import swbg.solutions.com.maintenanceapp.entity.InspectionResult
import swbg.solutions.com.maintenanceapp.entity.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class CompletedInspectionReportResponse(
    val reportId: UUID,
    val reportNumber: String,
    val reportType: InspectionReportType,
    val reportStatus: InspectionReportStatus,
    val fileName: String?,
    val contentType: String?,
    val storagePath: String?,
    val generatedAt: LocalDateTime?,
    val finalizedAt: LocalDateTime?,
    val items: List<InspectionReportItemDto>
)

data class InspectionReportItemDto(
    val id: UUID,
    val inspectionTaskId: UUID,
    val equipmentId: UUID,
    val equipmentName: String,
    val engineerId: UUID,
    val engineerName: String,
    val plannedDate: LocalDate,
    val observations: String,
    val result: InspectionResult,
    val performedAt: LocalDateTime,
    val createdAt: LocalDateTime
)

data class CreateInspectionReportRequest(
    val reportType: InspectionReportType,
    val reportTemplateCode: String = "DEFAULT",
    val items: List<CreateInspectionReportItemRequest>
)

data class CreateInspectionReportItemRequest(
    val inspectionTaskId: UUID,
    val engineerId: UUID,
    val result: InspectionResult,
    val observations: String,
    val performedAt: LocalDateTime? = null
)

data class FinalizeInspectionReportRequest(
    val fileName: String,
    val contentType: String = "application/pdf",
    val storagePath: String,
    val fileSizeBytes: Long
)

data class InspectionReportResponse(
    val id: UUID,
    val reportNumber: String,
    val reportType: InspectionReportType,
    val status: InspectionReportStatus,
    val reportTemplateCode: String,
    val fileName: String?,
    val contentType: String?,
    val storagePath: String?,
    val fileSizeBytes: Long?,
    val generatedAt: LocalDateTime?,
    val finalizedAt: LocalDateTime?,
    val items: List<InspectionReportItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val inspectionReportId: UUID?
)

data class InspectionReportItemResponse(
    val id: UUID,
    val inspectionTask: InspectionTaskSummaryResponse,
    val engineer: EngineerSummaryResponse,
    val result: InspectionResult,
    val observations: String,
    val performedAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class InspectionTaskSummaryResponse(
    val id: UUID,
    val equipment: EquipmentSummaryResponse,
    val plannedDate: LocalDate,
    val status: TaskStatus
)
