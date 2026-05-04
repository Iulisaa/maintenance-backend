package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.PlanningRequest
import swbg.solutions.com.maintenanceapp.dto.PlanningResult
import swbg.solutions.com.maintenanceapp.service.PlanningService
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/planning")
class PlanningController(
    private val planningService: PlanningService
) {

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    fun generatePlan(
        @RequestBody @Valid request: PlanningRequest
    ): PlanningResult {
        return planningService.generatePlan(request)
    }

    @PostMapping("/equipments/{equipmentId}/generate")
    @ResponseStatus(HttpStatus.CREATED)
    fun generatePlanForEquipment(
        @PathVariable equipmentId: UUID,
        @RequestBody @Valid request: PlanningRequest
    ): PlanningResult {
        return planningService.generatePlanForEquipment(
            equipmentId = equipmentId,
            request = request
        )
    }

    @PostMapping("/generate-window")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateWindow(
        @RequestParam(defaultValue = "30") days: Long
    ): PlanningResult {
        require(days > 0) {
            "days must be greater than 0"
        }

        val today = LocalDate.now()

        return planningService.generatePlan(
            PlanningRequest(
                startDate = today,
                endDate = today.plusDays(days)
            )
        )
    }

    @PostMapping("/equipments/{equipmentId}/generate-window")
    @ResponseStatus(HttpStatus.CREATED)
    fun generateWindowForEquipment(
        @PathVariable equipmentId: UUID,
        @RequestParam(defaultValue = "30") days: Long
    ): PlanningResult {
        require(days > 0) {
            "days must be greater than 0"
        }

        val today = LocalDate.now()

        return planningService.generatePlanForEquipment(
            equipmentId = equipmentId,
            request = PlanningRequest(
                startDate = today,
                endDate = today.plusDays(days)
            )
        )
    }

}
