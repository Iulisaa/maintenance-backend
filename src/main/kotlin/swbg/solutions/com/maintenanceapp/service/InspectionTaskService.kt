package swbg.solutions.com.maintenanceapp.service

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.CreateManualInspectionTaskRequest
import swbg.solutions.com.maintenanceapp.dto.EquipmentInspectionHistoryResponse
import swbg.solutions.com.maintenanceapp.dto.InspectionTaskResponse
import swbg.solutions.com.maintenanceapp.entity.*
import swbg.solutions.com.maintenanceapp.mapper.toResponse
import swbg.solutions.com.maintenanceapp.repository.InspectionReportItemRepository
import swbg.solutions.com.maintenanceapp.repository.InspectionTaskRepository
import java.time.LocalDate
import java.util.*

@Service
class InspectionTaskService(
    private val inspectionTaskRepository: InspectionTaskRepository,
    private val inspectionReportItemRepository: InspectionReportItemRepository,
    private val equipmentService: EquipmentService,
    private val engineerService: EngineerService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getHistoryForTask(taskId: UUID): List<EquipmentInspectionHistoryResponse> {
        val task = inspectionTaskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Inspection task not found: $taskId") }

        return inspectionReportItemRepository.findEquipmentHistoryForTask(
            equipmentId = task.equipment.id,
            currentTaskId = task.id
        )
    }

    fun getAllInRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<InspectionTaskResponse> {
        return inspectionTaskRepository
            .findAllByPlannedDateBetween(startDate, endDate)
            .map { it.toResponse() }
    }

    fun getById(id: UUID): InspectionTask {
        return inspectionTaskRepository.findById(id)
            .orElseThrow { NotFoundException("Task $id not found") }
    }

    fun getByIdResponse(id: UUID): InspectionTaskResponse {
        return getById(id).toResponse()
    }

    @Transactional
    fun createManual(request: CreateManualInspectionTaskRequest): InspectionTaskResponse {
        log.info(
            "Creating manual inspection task. equipmentId={}, assignedEngineerId={}, plannedDate={}",
            request.equipmentId,
            request.assignedEngineerId,
            request.plannedDate
        )

        val equipment = equipmentService.getEntityById(request.equipmentId)
        val engineer = request.assignedEngineerId
            ?.let { engineerService.getById(it) }

        val task = inspectionTaskRepository.save(
            InspectionTask(
                equipment = equipment,
                assignedEngineer = engineer,
                plannedDate = request.plannedDate,
                plannedYear = request.plannedDate.year,
                occurrenceNumber = null,
                generationKey = null,
                source = TaskSource.MANUAL,
                status = if (engineer != null) TaskStatus.ASSIGNED else TaskStatus.PLANNED
            )
        )

        log.info(
            "Manual inspection task saved. taskId={}, equipmentId={}, assignedEngineerId={}, plannedDate={}",
            task.id,
            task.equipment.id,
            task.assignedEngineer?.id,
            task.plannedDate
        )

        return task.toResponse()
    }

    @Transactional
    fun createGeneratedTask(
        equipmentId: UUID,
        plannedDate: LocalDate,
        occurrenceNumber: Int,
        generationKey: String
    ): InspectionTaskResponse {
        require(occurrenceNumber > 0) {
            "occurrenceNumber must be greater than 0"
        }

        require(generationKey.isNotBlank()) {
            "generationKey must not be blank"
        }

        val equipment = equipmentService.getEntityById(equipmentId)

        val assignedEngineer = equipment.defaultEngineer

        val task = inspectionTaskRepository.save(
            InspectionTask(
                equipment = equipment,
                assignedEngineer = assignedEngineer,
                plannedDate = plannedDate,
                plannedYear = plannedDate.year,
                occurrenceNumber = occurrenceNumber,
                generationKey = generationKey,
                source = TaskSource.GENERATED,
                status = if (assignedEngineer != null) TaskStatus.ASSIGNED else TaskStatus.PLANNED
            )
        )

        return task.toResponse()
    }

    @Transactional
    fun reassignTask(
        taskId: UUID,
        engineerId: UUID
    ): InspectionTaskResponse {
        val task = getById(taskId)
        val engineer = engineerService.getById(engineerId)

        require(task.status != TaskStatus.COMPLETED) {
            "Completed tasks cannot be reassigned"
        }

        require(task.status != TaskStatus.CANCELLED) {
            "Cancelled tasks cannot be reassigned"
        }

        task.assignedEngineer = engineer
        task.status = TaskStatus.ASSIGNED

        return inspectionTaskRepository.save(task).toResponse()
    }

    @Transactional
    fun moveTask(
        taskId: UUID,
        plannedDate: LocalDate
    ): InspectionTaskResponse {
        val task = getById(taskId)

        require(task.status != TaskStatus.COMPLETED) {
            "Completed tasks cannot be moved"
        }

        require(task.status != TaskStatus.CANCELLED) {
            "Cancelled tasks cannot be moved"
        }

        task.plannedDate = plannedDate
        task.plannedYear = plannedDate.year

        return inspectionTaskRepository.save(task).toResponse()
    }

    @Transactional
    fun cancelTask(taskId: UUID): InspectionTaskResponse {
        val task = getById(taskId)

        require(task.status != TaskStatus.COMPLETED) {
            "Completed tasks cannot be cancelled"
        }

        task.status = TaskStatus.CANCELLED

        return inspectionTaskRepository.save(task).toResponse()
    }

    fun countActiveTasksForEngineerOnDate(
        engineerId: UUID,
        date: LocalDate
    ): Long {
        return inspectionTaskRepository.countByAssignedEngineerIdAndPlannedDateAndStatusNot(
            engineerId,
            date,
            TaskStatus.CANCELLED
        )
    }

    fun hasGeneratedTaskForEquipment(
        equipmentId: UUID,
        generationKey: String
    ): Boolean {
        return inspectionTaskRepository.existsByEquipmentIdAndGenerationKey(
            equipmentId,
            generationKey
        )
    }

    fun getTasksForEngineerInRangeForAgenda(
        engineerId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<InspectionTaskResponse> {
        return inspectionTaskRepository
            .findAllByAssignedEngineerIdAndPlannedDateBetween(
                assignedEngineerId = engineerId,
                startDate = startDate,
                endDate = endDate
            )
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getTasksForEngineer(
        engineerId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        filter: InspectionTaskFilter
    ): List<InspectionTaskResponse> {
        val tasks = when (filter) {
            InspectionTaskFilter.PLANNED ->
                inspectionTaskRepository.findForEngineerInRangeAndStatusIn(
                    engineerId = engineerId,
                    startDate = startDate,
                    endDate = endDate,
                    statuses = listOf(TaskStatus.PLANNED, TaskStatus.ASSIGNED)
                )

            InspectionTaskFilter.COMPLETED ->
                inspectionTaskRepository.findForEngineerInRangeAndStatusIn(
                    engineerId = engineerId,
                    startDate = startDate,
                    endDate = endDate,
                    statuses = listOf(TaskStatus.COMPLETED)
                )

            InspectionTaskFilter.FAILED ->
                inspectionTaskRepository.findForEngineerInRangeAndResult(
                    engineerId = engineerId,
                    startDate = startDate,
                    endDate = endDate,
                    result = InspectionResult.FAILED
                )

            InspectionTaskFilter.FOLLOW_UP ->
                inspectionTaskRepository.findForEngineerInRangeAndResult(
                    engineerId = engineerId,
                    startDate = startDate,
                    endDate = endDate,
                    result = InspectionResult.FOLLOW_UP
                )

            InspectionTaskFilter.ALL ->
                inspectionTaskRepository.findForEngineerInRange(
                    engineerId = engineerId,
                    startDate = startDate,
                    endDate = endDate
                )
        }
        val taskIds = tasks.mapNotNull { it.id }

        val reportItemsByTaskId = if (taskIds.isEmpty()) {
            emptyMap()
        } else {
            inspectionReportItemRepository
                .findByInspectionTaskIdsWithReport(taskIds)
                .associateBy { requireNotNull(it.inspectionTask.id) }
        }

        return tasks.map { task ->
            val reportItem = reportItemsByTaskId[task.id]

            task.toResponse(
                result = reportItem?.result,
                inspectionReportId = reportItem?.inspectionReport?.id
            )
        }
    }
}
