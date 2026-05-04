package swbg.solutions.com.maintenanceapp.dto

import java.time.LocalDateTime
import java.util.*

data class CreateEngineerRequest(
    val name: String,
    val email: String,
    val maxTasksPerDay: Int
)

data class UpdateEngineerRequest(
    val fullName: String,
    val email: String,
    val active: Boolean,
    val maxTasksPerDay: Int
)

data class EngineerResponse(
    val id: UUID? = null,
    val name: String,
    val email: String,
    val active: Boolean,
    val maxTasksPerDay: Int,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class EngineerSummaryResponse(
    val id: UUID,
    val fullName: String,
    val email: String
)
