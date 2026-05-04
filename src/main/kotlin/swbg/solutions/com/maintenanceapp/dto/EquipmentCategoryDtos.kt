package swbg.solutions.com.maintenanceapp.dto

import java.util.*

data class EquipmentCategoryResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val active: Boolean,
)

data class CreateEquipmentCategoryRequest(
    val name: String,
    val description: String? = null,
    val active: Boolean = true,
)