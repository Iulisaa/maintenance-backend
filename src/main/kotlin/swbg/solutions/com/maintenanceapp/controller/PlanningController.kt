package swbg.solutions.com.maintenanceapp.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import swbg.solutions.com.maintenanceapp.utils.PlanningRequest
import swbg.solutions.com.maintenanceapp.utils.PlanningResult
import swbg.solutions.com.maintenanceapp.service.PlanningService

@RestController
@RequestMapping("/api/planning")
class PlanningController(
    private val planningService: PlanningService
) {
    @PostMapping("/generate")
    fun generatePlan(@RequestBody request: PlanningRequest): PlanningResult =
        planningService.generatePlan(request)
}