package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import swbg.solutions.com.maintenanceapp.dto.CreateEquipmentCategoryRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentCategoryResponse
import swbg.solutions.com.maintenanceapp.entity.EquipmentCategory
import swbg.solutions.com.maintenanceapp.repository.EquipmentCategoryRepository

@Service
class EquipmentCategoryService(
    private val categoryRepository: EquipmentCategoryRepository,
) {

    fun getActiveCategories(): List<EquipmentCategoryResponse> {
        return categoryRepository.findAllByActiveTrueOrderByNameAsc()
            .map { it.toResponse() }
    }

    fun createCategory(request: CreateEquipmentCategoryRequest): EquipmentCategoryResponse {
        val name = request.name.trim()

        require(name.isNotBlank()) {
            "Category name is required."
        }

        require(!categoryRepository.existsByNameIgnoreCase(name)) {
            "Category already exists."
        }

        val category = EquipmentCategory(
            name = name,
            description = request.description?.trim()?.takeIf { it.isNotBlank() },
            active = request.active,
        )

        return categoryRepository.save(category).toResponse()
    }

    private fun EquipmentCategory.toResponse(): EquipmentCategoryResponse {
        return EquipmentCategoryResponse(
            id = requireNotNull(id),
            name = name,
            description = description,
            active = active,
        )
    }
}
