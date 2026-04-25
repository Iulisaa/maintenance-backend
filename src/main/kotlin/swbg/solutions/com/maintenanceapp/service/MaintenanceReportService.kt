package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.CompleteTaskRequest
import swbg.solutions.com.maintenanceapp.dto.MaintenanceReportResponse
import swbg.solutions.com.maintenanceapp.entity.MaintenanceReport
import swbg.solutions.com.maintenanceapp.repository.MaintenanceReportRepository
import swbg.solutions.com.maintenanceapp.repository.MaintenanceTaskRepository
import swbg.solutions.com.maintenanceapp.utils.PdfGenerator
import swbg.solutions.com.maintenanceapp.utils.TaskStatus
import java.time.LocalDateTime
import java.util.*

@Service
class MaintenanceReportService(
    private val maintenanceReportRepository: MaintenanceReportRepository,
    private val maintenanceTaskRepository: MaintenanceTaskRepository,
    private val pdfGenerator: PdfGenerator
) {
    @Transactional
    fun completeTaskAndGeneratePdf(
        taskId: UUID,
        request: CompleteTaskRequest
    ): Pair<String, ByteArray> {
        val task = maintenanceTaskRepository.findById(taskId)
            .orElseThrow { NotFoundException("Task $taskId not found") }

        require(task.status == TaskStatus.PLANNED) {
            "Only PLANNED tasks can be completed. Current status: ${task.status}"
        }

        if (maintenanceReportRepository.existsByTaskId(taskId)) {
            throw IllegalStateException("Task $taskId already has a report")
        }

        val now = LocalDateTime.now()
        val performedAt = request.performedAt ?: now

        val report = maintenanceReportRepository.save(
            MaintenanceReport(
                task = task,
                observations = request.observations.trim(),
                result = request.result,
                performedAt = performedAt,
                createdAt = now
            )
        )

        task.status = TaskStatus.COMPLETED
        task.completedAt = performedAt
        maintenanceTaskRepository.save(task)

        val pdfBytes = pdfGenerator.generateMaintenanceReportPdf(task, report)

        val fileName = "maintenance-report-${sanitizeFileName(task.equipment.code)}-${task.scheduledDate}.pdf"

        return fileName to pdfBytes
    }

    private fun sanitizeFileName(value: String): String {
        return value.replace(Regex("[^A-Za-z0-9._-]"), "_")
    }

    @Transactional
    fun completeTask(
        taskId: UUID,
        request: CompleteTaskRequest
    ): MaintenanceReportResponse {
        val task = maintenanceTaskRepository.findById(taskId)
            .orElseThrow { NotFoundException("Task $taskId not found") }

        require(task.status == TaskStatus.PLANNED) {
            "Only PLANNED tasks can be completed. Current status: ${task.status}"
        }

        if (maintenanceReportRepository.existsByTaskId(taskId)) {
            throw IllegalStateException("Task $taskId already has a report")
        }

        val now = LocalDateTime.now()
        val performedAt = request.performedAt ?: now

        val report = maintenanceReportRepository.save(
            MaintenanceReport(
                task = task,
                observations = request.observations.trim(),
                result = request.result,
                performedAt = performedAt,
                createdAt = now
            )
        )

        task.status = TaskStatus.COMPLETED
        task.completedAt = performedAt

        maintenanceTaskRepository.save(task)

        return report.toResponse()
    }

    private fun MaintenanceReport.toResponse(): MaintenanceReportResponse {
        return MaintenanceReportResponse(
            id = requireNotNull(id),
            taskId = requireNotNull(task.id),
            equipmentId = requireNotNull(task.equipment.id),
            equipmentName = task.equipment.name,
            engineerId = requireNotNull(task.assignedEngineer.id),
            engineerName = requireNotNull(task.assignedEngineer.fullName) {
                "Assigned engineer fullName cannot be null"
            },
            scheduledDate = task.scheduledDate,
            observations = observations,
            result = result,
            performedAt = performedAt,
            createdAt = createdAt
        )
    }
}
