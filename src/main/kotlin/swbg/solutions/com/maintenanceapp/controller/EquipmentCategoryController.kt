package swbg.solutions.com.maintenanceapp.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import swbg.solutions.com.maintenanceapp.dto.CreateEquipmentCategoryRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentCategoryResponse
import swbg.solutions.com.maintenanceapp.service.EquipmentCategoryService

@RestController
@RequestMapping("/api/equipment-categories")
class EquipmentCategoryController(
    private val categoryService: EquipmentCategoryService,
) {

    @GetMapping
    fun getCategories(): List<EquipmentCategoryResponse> {
        return categoryService.getActiveCategories()
    }

    @PostMapping
    fun createCategory(
        @RequestBody request: CreateEquipmentCategoryRequest,
    ): EquipmentCategoryResponse {
        return categoryService.createCategory(request)
    }
}
