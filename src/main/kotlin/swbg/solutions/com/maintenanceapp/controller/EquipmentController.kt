package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.CreateEquipmentRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentResponse
import swbg.solutions.com.maintenanceapp.dto.UpdateEquipmentRequest
import swbg.solutions.com.maintenanceapp.service.EquipmentService
import java.util.*

@RestController
@RequestMapping("/api/equipments")
class EquipmentController(
    private val equipmentService: EquipmentService
) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) active: Boolean?
    ): List<EquipmentResponse> {
        return equipmentService.getAll(active)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID
    ): EquipmentResponse {
        return equipmentService.getById(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid request: CreateEquipmentRequest
    ): EquipmentResponse {
        return equipmentService.create(request)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UpdateEquipmentRequest
    ): EquipmentResponse {
        return equipmentService.update(id, request)
    }

    @PatchMapping("/{id}/activate")
    fun activate(
        @PathVariable id: UUID
    ): EquipmentResponse {
        return equipmentService.activate(id)
    }

    @PatchMapping("/{id}/deactivate")
    fun deactivate(
        @PathVariable id: UUID
    ): EquipmentResponse {
        return equipmentService.deactivate(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID
    ) {
        equipmentService.deactivate(id)
    }
}
