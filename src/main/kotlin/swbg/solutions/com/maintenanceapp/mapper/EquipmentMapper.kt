package swbg.solutions.com.maintenanceapp.mapper

import swbg.solutions.com.maintenanceapp.dto.EquipmentResponse
import swbg.solutions.com.maintenanceapp.dto.EquipmentSummaryResponse
import swbg.solutions.com.maintenanceapp.entity.Equipment
import swbg.solutions.com.maintenanceapp.entity.EquipmentActiveMonth

fun Equipment.toSummaryResponse(): EquipmentSummaryResponse {
    return EquipmentSummaryResponse(
        id = requireNotNull(id),
        name = name,
        zone = code,
        seasonType = seasonType
    )
}

fun Equipment.toResponse(activeMonths: Set<Int>): EquipmentResponse {
    return EquipmentResponse(
        id = requireNotNull(id),
        name = name,
        code = code,
        active = active,
        seasonType = seasonType,
        frequencyPerYear = frequencyPerYear,
        estimatedDurationMinutes = estimatedDurationMinutes,
        serialNumber = serialNumber,
        notes = notes,
        defaultEngineer = defaultEngineer?.toSummaryResponse(),
        activeMonths = activeMonths,
        createdAt = createdAt,
        updatedAt = updatedAt,
        categoryId = category.id,
        categoryName = category.name
    )
}

fun Collection<EquipmentActiveMonth>.toMonthNumbers(): Set<Int> {
    return mapNotNull { it.id.monthNumber }.toSet()
}
