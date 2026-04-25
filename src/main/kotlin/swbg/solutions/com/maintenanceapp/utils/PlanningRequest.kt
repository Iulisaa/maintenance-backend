package swbg.solutions.com.maintenanceapp.utils

import java.time.LocalDate

data class PlanningRequest(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class PlanningResult(
    val generatedTasks: Int,
    val skippedEquipments: List<String>
)
