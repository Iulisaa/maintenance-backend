package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.*
import swbg.solutions.com.maintenanceapp.entity.InspectionTaskFilter
import swbg.solutions.com.maintenanceapp.service.InspectionTaskService
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/inspection-tasks")
class InspectionTaskController(
    private val inspectionTaskService: InspectionTaskService
) {
    @GetMapping("/{taskId}/equipment-history")
    fun getEquipmentHistoryForTask(
        @PathVariable taskId: UUID
    ): List<EquipmentInspectionHistoryResponse> {
        return inspectionTaskService.getHistoryForTask(taskId)
    }

    @GetMapping
    fun getAllInRange(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate
    ): List<InspectionTaskResponse> {
        require(!endDate.isBefore(startDate)) {
            "endDate cannot be before startDate"
        }

        return inspectionTaskService.getAllInRange(
            startDate = startDate,
            endDate = endDate
        )
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID
    ): InspectionTaskResponse {
        return inspectionTaskService.getByIdResponse(id)
    }

    /**
     * Manual task created by admin.
     * Planner-generated tasks should be created by PlanningService, not this endpoint.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createManualTask(
        @RequestBody @Valid request: CreateManualInspectionTaskRequest
    ): InspectionTaskResponse {
        return inspectionTaskService.createManual(request)
    }

    @PatchMapping("/{id}/reassign")
    fun reassignTask(
        @PathVariable id: UUID,
        @RequestBody @Valid request: ReassignInspectionTaskRequest
    ): InspectionTaskResponse {
        return inspectionTaskService.reassignTask(
            taskId = id,
            engineerId = request.assignedEngineerId
        )
    }

    @PatchMapping("/{id}/move")
    fun moveTask(
        @PathVariable id: UUID,
        @RequestBody @Valid request: MoveInspectionTaskRequest
    ): InspectionTaskResponse {
        return inspectionTaskService.moveTask(
            taskId = id,
            plannedDate = request.plannedDate
        )
    }

    @PatchMapping("/{id}/cancel")
    fun cancelTask(
        @PathVariable id: UUID
    ): InspectionTaskResponse {
        return inspectionTaskService.cancelTask(id)
    }

    /**
     * Agenda endpoint: all tasks for one engineer in a date range.
     */
    @GetMapping("/engineers/{engineerId}/agenda")
    fun getTasksForEngineerAgenda(
        @PathVariable engineerId: UUID,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate
    ): List<InspectionTaskResponse> {
        require(!endDate.isBefore(startDate)) {
            "endDate cannot be before startDate"
        }

        return inspectionTaskService.getTasksForEngineerInRangeForAgenda(
            engineerId = engineerId,
            startDate = startDate,
            endDate = endDate
        )
    }

    /**
     * Flexible engineer task filter.
     *
     * Examples:
     * /api/inspection-tasks/engineers/{id}?date=2026-05-01
     * /api/inspection-tasks/engineers/{id}?status=ASSIGNED
     * /api/inspection-tasks/engineers/{id}?result=FAILED
     */
    @GetMapping("/engineers/{engineerId}")
    fun getTasksForEngineer(
        @PathVariable engineerId: UUID,
        @RequestParam startDate: LocalDate,
        @RequestParam endDate: LocalDate,
        @RequestParam(defaultValue = "PLANNED") filter: InspectionTaskFilter
    ): List<InspectionTaskResponse> {
        return inspectionTaskService.getTasksForEngineer(
            engineerId = engineerId,
            startDate = startDate,
            endDate = endDate,
            filter = filter
        )
    }

}
