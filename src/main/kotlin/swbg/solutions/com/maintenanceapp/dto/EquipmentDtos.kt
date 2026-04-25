package swbg.solutions.com.maintenanceapp.dto

import swbg.solutions.com.maintenanceapp.utils.EquipmentSeasonType
import java.util.*

data class CreateEquipmentRequest(
    val name: String,
    val code: String,
    val active: Boolean = true,
    val seasonType: EquipmentSeasonType,
    val serialNumber: String? = null,
    val notes: String? = null,
    val assignedEngineerId: UUID,
    val recurrencePerYear: Int,
    val estimatedDurationMinutes: Int,
    val reportTemplateCode: String = "DEFAULT"
)

data class EquipmentResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val active: Boolean,
    val seasonType: EquipmentSeasonType,
    val serialNumber: String?,
    val notes: String?,
    val assignedEngineerId: UUID,
    val assignedEngineerName: String,
    val recurrencePerYear: Int?,
    val estimatedDurationMinutes: Int?,
    val activeMonths: List<Int>,
    val reportTemplateCode: String?
)
