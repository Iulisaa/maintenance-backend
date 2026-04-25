package swbg.solutions.com.maintenanceapp.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateEngineerRequest(
    @field:NotBlank val fullName: String,
    @field:Email @field:NotBlank val email: String,
    val active: Boolean = true,
    val maxTasksPerDay: Int = 5
)

data class EngineerResponse(
    val id: UUID,
    val fullName: String,
    val email: String,
    val active: Boolean,
    val maxTasksPerDay: Int
)

data class UpdateEngineerRequest(
    val fullName: String,
    val email: String,
    val active: Boolean,
    val maxTasksPerDay: Int
)
