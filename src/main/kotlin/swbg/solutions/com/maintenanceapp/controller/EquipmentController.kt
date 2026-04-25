package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import swbg.solutions.com.maintenanceapp.dto.CreateEquipmentRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentResponse
import swbg.solutions.com.maintenanceapp.service.EquipmentService

@RestController
@RequestMapping("/api/equipments")
class EquipmentController(
    private val equipmentService: EquipmentService
) {
    @GetMapping
    fun getAll(): List<EquipmentResponse> = equipmentService.getAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: CreateEquipmentRequest): EquipmentResponse =
        equipmentService.create(request)
}