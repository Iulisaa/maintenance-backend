package swbg.solutions.com.maintenanceapp.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class MaintenanceReportResponse(
    val id: UUID,
    val taskId: UUID,
    val equipmentId: UUID,
    val equipmentName: String,
    val engineerId: UUID,
    val engineerName: String,
    val scheduledDate: LocalDate,
    val observations: String,
    val result: MaintenanceResult,
    val performedAt: LocalDateTime,
    val createdAt: LocalDateTime
)