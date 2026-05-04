package swbg.solutions.com.maintenanceapp.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import swbg.solutions.com.maintenanceapp.entity.EquipmentSeasonType
import swbg.solutions.com.maintenanceapp.entity.InspectionResult
import java.time.LocalDateTime
import java.util.*

data class CreateEquipmentRequest(

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val code: String,

    val active: Boolean = true,

    val seasonType: EquipmentSeasonType,

    @field:Min(1)
    val frequencyPerYear: Int,

    @field:Min(1)
    val estimatedDurationMinutes: Int,

    val serialNumber: String? = null,

    val notes: String? = null,

    val defaultEngineerId: UUID? = null,

    @field:NotEmpty
    val activeMonths: Set<@Min(1) @Max(12) Int>,
    val categoryId: UUID,
)

data class UpdateEquipmentRequest(

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val code: String,

    val active: Boolean,

    val seasonType: EquipmentSeasonType,

    @field:Min(1)
    val frequencyPerYear: Int,

    @field:Min(1)
    val estimatedDurationMinutes: Int,

    val serialNumber: String? = null,

    val notes: String? = null,

    val defaultEngineerId: UUID? = null,

    @field:NotEmpty
    val activeMonths: Set<@Min(1) @Max(12) Int>,
    @field:NotNull
    val categoryId: UUID,

)

data class EquipmentResponse(
    val id: UUID,
    val name: String,
    val code: String,
    val active: Boolean,
    val seasonType: EquipmentSeasonType,
    val frequencyPerYear: Int,
    val estimatedDurationMinutes: Int,
    val serialNumber: String?,
    val notes: String?,
    val defaultEngineer: EngineerSummaryResponse?,
    val activeMonths: Set<Int>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val categoryId: UUID?,
    val categoryName: String?,
)

data class EquipmentInspectionHistoryResponse(
    val performedAt: LocalDateTime?,
    val observations: String?,
    val result: InspectionResult?,
    val engineerName: String?,
    val reportNumber: String?,
    val fileName: String?
)
