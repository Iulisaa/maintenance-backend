package swbg.solutions.com.maintenanceapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import swbg.solutions.com.maintenanceapp.entity.InspectionResult
import java.time.LocalDateTime
import java.util.*

data class CompleteTaskRequest(
    @field:NotBlank
    val observations: String,
    @field:NotNull
    val result: InspectionResult,
    val performedAt: LocalDateTime? = null,
    val reportTemplateCode: String = "DEFAULT"
)

data class CompleteTasksRequest(
    @field:NotEmpty
    val items: List<CompleteTaskItemRequest>,
    val performedAt: LocalDateTime? = null,
    val reportTemplateCode: String = "DEFAULT"
)

data class CompleteTaskItemRequest(
    @field:NotNull
    val taskId: UUID,
    @field:NotBlank
    val observations: String,
    @field:NotNull
    val result: InspectionResult
)
