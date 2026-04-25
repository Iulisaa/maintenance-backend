package swbg.solutions.com.maintenanceapp.dto

import jakarta.validation.constraints.NotNull
import swbg.solutions.com.maintenanceapp.utils.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class AssignTaskRequest(
    @field:NotNull val equipmentId: UUID,
    @field:NotNull val assignedEngineerId: UUID,
    @field:NotNull val scheduledDate: LocalDate
)

data class MaintenanceTaskResponse(
    val id: UUID,
    val equipmentId: UUID,
    val equipmentName: String,
    val engineerId: UUID,
    val engineerName: String,
    val scheduledDate: LocalDate,
    val status: TaskStatus,
    val generatedByPlanner: Boolean,
    val completedAt: LocalDateTime?
)
