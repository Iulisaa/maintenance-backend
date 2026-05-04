package swbg.solutions.com.maintenanceapp.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class PlanningRequest(

    @field:NotNull
    val startDate: LocalDate,

    @field:NotNull
    val endDate: LocalDate
)

data class PlanningResult(
    val generatedTasks: Int,
    val skippedEquipments: List<String>
)
