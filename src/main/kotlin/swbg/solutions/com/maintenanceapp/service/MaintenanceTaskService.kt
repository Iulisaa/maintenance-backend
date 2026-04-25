package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.AssignTaskRequest
import swbg.solutions.com.maintenanceapp.dto.MaintenanceTaskResponse
import swbg.solutions.com.maintenanceapp.entity.MaintenanceTask
import swbg.solutions.com.maintenanceapp.repository.MaintenanceTaskRepository
import swbg.solutions.com.maintenanceapp.utils.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class MaintenanceTaskService(
    private val maintenanceTaskRepository: MaintenanceTaskRepository,
    private val equipmentService: EquipmentService,
    private val engineerService: EngineerService
) {
    fun getAllInRange(startDate: LocalDate, endDate: LocalDate): List<MaintenanceTaskResponse> =
        maintenanceTaskRepository.findAllByScheduledDateBetween(startDate, endDate).map { it.toResponse() }

    fun getById(id: UUID): MaintenanceTask =
        maintenanceTaskRepository.findById(id).orElseThrow { NotFoundException("Task $id not found") }

    @Transactional
    fun create(request: AssignTaskRequest, generatedByPlanner: Boolean = false): MaintenanceTaskResponse {
        val equipment = equipmentService.getEntityById(request.equipmentId)
        val engineer = engineerService.getById(request.assignedEngineerId)

        val task = maintenanceTaskRepository.save(
            MaintenanceTask(
                equipment = equipment,
                assignedEngineer = engineer,
                scheduledDate = request.scheduledDate,
                generatedByPlanner = generatedByPlanner
            )
        )

        return task.toResponse()
    }

    @Transactional
    fun markCompleted(taskId: UUID) {
        val task = getById(taskId)
        task.status = TaskStatus.COMPLETED
        task.completedAt = LocalDateTime.now()
        maintenanceTaskRepository.save(task)
    }

    fun countActiveTasksForEngineerOnDate(engineerId: UUID, date: LocalDate): Long =
        maintenanceTaskRepository.countByAssignedEngineerIdAndScheduledDateAndStatusNot(
            engineerId,
            date,
            TaskStatus.CANCELLED
        )

    fun hasTaskForEquipmentOnDate(equipmentId: UUID, date: LocalDate): Boolean =
        maintenanceTaskRepository.existsByEquipmentIdAndScheduledDate(equipmentId, date)

    private fun MaintenanceTask.toResponse() =
        MaintenanceTaskResponse(
            id = requireNotNull(id),
            equipmentId = requireNotNull(equipment.id),
            equipmentName = equipment.name,
            engineerId = requireNotNull(assignedEngineer.id),
            engineerName = assignedEngineer.fullName,
            scheduledDate = scheduledDate,
            status = status,
            generatedByPlanner = generatedByPlanner,
            completedAt = completedAt
        )

    fun getByIdResponse(id: UUID): MaintenanceTaskResponse =
        getById(id).toResponse()

    fun getPendingTasksForEngineer(engineerId: UUID, date: LocalDate?): List<MaintenanceTaskResponse> {
        val tasks = if (date != null) {
            maintenanceTaskRepository
                .findAllByAssignedEngineerIdAndScheduledDateAndStatus(
                    engineerId,
                    date,
                    TaskStatus.PLANNED
                )
        } else {
            maintenanceTaskRepository
                .findAllByAssignedEngineerIdAndStatus(
                    engineerId,
                    TaskStatus.PLANNED
                )
        }

        return tasks.map { it.toResponse() }
    }


    @Transactional
    fun cancel(taskId: UUID) {
        val task = getById(taskId)
        task.status = TaskStatus.CANCELLED
        maintenanceTaskRepository.save(task)
    }
}