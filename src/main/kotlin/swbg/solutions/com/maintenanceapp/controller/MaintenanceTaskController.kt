package swbg.solutions.com.maintenanceapp.controller

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.MaintenanceTaskResponse
import swbg.solutions.com.maintenanceapp.service.MaintenanceTaskService
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/tasks")
class MaintenanceTaskController(
    private val maintenanceTaskService: MaintenanceTaskService
) {
    @GetMapping("/engineers/{engineerId}/pending")
    fun getPendingTasksForEngineer(
        @PathVariable engineerId: UUID,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): List<MaintenanceTaskResponse> =
        maintenanceTaskService.getPendingTasksForEngineer(engineerId, date)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): MaintenanceTaskResponse =
        maintenanceTaskService.getByIdResponse(id)
}