package swbg.solutions.com.maintenanceapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CompleteTaskRequest(
    @field:NotBlank
    @field:Size(max = 5000)
    val observations: String,

    @field:NotNull
    val result: MaintenanceResult,

    val performedAt: LocalDateTime? = null
)

enum class MaintenanceResult {
    PASSED,
    FAILED,
    REQUIRES_FOLLOW_UP
}
