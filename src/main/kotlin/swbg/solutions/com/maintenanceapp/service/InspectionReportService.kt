package swbg.solutions.com.maintenanceapp.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.common.exception.NotFoundException
import swbg.solutions.com.maintenanceapp.dto.*
import swbg.solutions.com.maintenanceapp.email.ReportEmailService
import swbg.solutions.com.maintenanceapp.entity.*
import swbg.solutions.com.maintenanceapp.mapper.toResponse
import swbg.solutions.com.maintenanceapp.repository.InspectionReportItemRepository
import swbg.solutions.com.maintenanceapp.repository.InspectionReportRepository
import swbg.solutions.com.maintenanceapp.repository.InspectionTaskRepository
import swbg.solutions.com.maintenanceapp.utils.PdfGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class InspectionReportService(
    private val inspectionReportRepository: InspectionReportRepository,
    private val inspectionReportItemRepository: InspectionReportItemRepository,
    private val inspectionTaskRepository: InspectionTaskRepository,
    private val pdfGenerator: PdfGenerator,
    private val engineerService: EngineerService,
    private val reportEmailService: ReportEmailService
) {

    private fun createReportItem(
        report: InspectionReport,
        request: CreateInspectionReportItemRequest,
        now: LocalDateTime
    ): InspectionReportItem {
        val task = inspectionTaskRepository.findById(request.inspectionTaskId)
            .orElseThrow { NotFoundException("Task ${request.inspectionTaskId} not found") }

        require(task.status == TaskStatus.PLANNED || task.status == TaskStatus.ASSIGNED) {
            "Only PLANNED or ASSIGNED tasks can be completed. Task ${task.id} current status: ${task.status}"
        }

        if (inspectionReportItemRepository.existsByInspectionTaskId(requireNotNull(task.id))) {
            throw IllegalStateException("Task ${task.id} already has an inspection report item")
        }

        val engineer = engineerService.getById(request.engineerId)

        require(request.observations.isNotBlank()) {
            "observations must not be blank"
        }

        return InspectionReportItem(
            inspectionReport = report,
            inspectionTask = task,
            engineer = engineer,
            observations = request.observations.trim(),
            result = request.result,
            performedAt = request.performedAt ?: now
        )
    }

    @Transactional(readOnly = true)
    fun getByTaskId(taskId: UUID): InspectionReportResponse {
        val item = inspectionReportItemRepository.findByInspectionTaskId(taskId)
            ?: throw NotFoundException("No inspection report found for task $taskId")

        return item.inspectionReport.toResponse(
            items = listOf(item)
        )
    }

    @Transactional
    fun createReport(
        request: CreateInspectionReportRequest
    ): InspectionReportResponse {
        require(request.items.isNotEmpty()) {
            "Report must contain at least one item"
        }

        if (request.reportType == InspectionReportType.SINGLE_EQUIPMENT) {
            require(request.items.size == 1) {
                "SINGLE_EQUIPMENT report must contain exactly one item"
            }
        }

        if (request.reportType == InspectionReportType.MULTI_EQUIPMENT) {
            require(request.items.size > 1) {
                "MULTI_EQUIPMENT report must contain at least two items"
            }
        }

        val now = LocalDateTime.now()
        val reportPerformedAt = request.items
            .mapNotNull { it.performedAt }
            .firstOrNull()
            ?: now
        val report = inspectionReportRepository.save(
            InspectionReport(
                reportNumber = generateReportNumber(
                    performedAt = reportPerformedAt
                ),
                reportType = request.reportType,
                status = InspectionReportStatus.DRAFT,
                reportTemplateCode = request.reportTemplateCode.ifBlank { "DEFAULT" }
            )
        )

        val items = request.items.map { itemRequest ->
            createReportItem(
                report = report,
                request = itemRequest,
                now = now
            )
        }

        val savedItems = inspectionReportItemRepository.saveAll(items)

        savedItems.forEach { item ->
            val task = item.inspectionTask
            task.status = TaskStatus.COMPLETED
            task.completedAt = item.performedAt
            inspectionTaskRepository.save(task)
        }

        return report.toResponse(savedItems)
    }

    @Transactional
    fun getById(reportId: UUID): InspectionReportResponse {
        val report = inspectionReportRepository.findById(reportId)
            .orElseThrow { NotFoundException("Inspection report $reportId not found") }

        val items = inspectionReportItemRepository.findAllByInspectionReportId(reportId)

        return report.toResponse(items)
    }

    @Transactional
    fun finalizeReport(
        reportId: UUID,
        request: FinalizeInspectionReportRequest
    ): InspectionReportResponse {
        val report = inspectionReportRepository.findById(reportId)
            .orElseThrow { NotFoundException("Inspection report $reportId not found") }

        require(report.status == InspectionReportStatus.DRAFT) {
            "Only DRAFT reports can be finalized. Current status: ${report.status}"
        }

        require(request.fileSizeBytes >= 0) {
            "fileSizeBytes must be greater than or equal to 0"
        }

        val now = LocalDateTime.now()

        report.fileName = request.fileName.trim()
        report.contentType = request.contentType.trim()
        report.storagePath = request.storagePath.trim()
        report.fileSizeBytes = request.fileSizeBytes
        report.generatedAt = now
        report.finalizedAt = now
        report.status = InspectionReportStatus.FINALIZED

        val savedReport = inspectionReportRepository.save(report)
        val items = inspectionReportItemRepository.findAllByInspectionReportId(reportId)

        return savedReport.toResponse(items)
    }

    @Transactional
    fun generatePdfForExistingReport(reportId: UUID): Pair<String, ByteArray> {
        val report = inspectionReportRepository.findById(reportId)
            .orElseThrow { NotFoundException("Inspection report $reportId not found") }

        val items = inspectionReportItemRepository.findAllByInspectionReportId(reportId)

        require(items.isNotEmpty()) {
            "Inspection report $reportId has no items"
        }

        val pdfBytes = pdfGenerator.generateInspectionReportPdf(
            report = report,
            items = items
        )

        val fileName = report.fileName
            ?: "inspection-report-${sanitizeFileName(report.reportNumber)}.pdf"

        return fileName to pdfBytes
    }

    private fun validateTaskCanBeCompleted(
        taskId: UUID,
        status: TaskStatus
    ) {
        require(status == TaskStatus.PLANNED || status == TaskStatus.ASSIGNED) {
            "Only PLANNED or ASSIGNED tasks can be completed. Task $taskId current status: $status"
        }
    }

    private fun generateReportNumber(
        performedAt: LocalDateTime
    ): String {
        val formattedDate = performedAt
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("ddMMyyyy"))

        val uniqueSuffix = UUID.randomUUID()
            .toString()
            .take(8)
            .uppercase()

        return "Proces-Verbal-KYS-$formattedDate-$uniqueSuffix"
    }

    private fun sanitizeFileName(value: String): String {
        return value.replace(Regex("[^A-Za-z0-9._-]"), "_")
    }

    private fun InspectionReport.toCompletedResponse(
        items: List<InspectionReportItem>,
        emailSent: Boolean
    ): CompletedInspectionReportResponse {
        return CompletedInspectionReportResponse(
            reportId = requireNotNull(id),
            reportNumber = reportNumber,
            reportType = reportType,
            reportStatus = status,
            fileName = fileName,
            contentType = contentType,
            storagePath = storagePath,
            generatedAt = generatedAt,
            finalizedAt = finalizedAt,
            items = items.map { it.toDto() }
        )
    }

    private fun InspectionReportItem.toDto(): InspectionReportItemDto {
        val task = inspectionTask
        val equipment = task.equipment

        return InspectionReportItemDto(
            id = requireNotNull(id),
            inspectionTaskId = requireNotNull(task.id),
            equipmentId = requireNotNull(equipment.id),
            equipmentName = equipment.name,
            engineerId = requireNotNull(engineer.id),
            engineerName = engineer.fullName,
            plannedDate = task.plannedDate,
            observations = observations,
            result = result,
            performedAt = performedAt,
            createdAt = createdAt
        )
    }

    @Transactional
    fun completeTask(
        taskId: UUID,
        request: CompleteTaskRequest
    ): CompletedInspectionReportResponse {
        return completeTasksInternal(
            items = listOf(
                CompleteTaskItemRequest(
                    taskId = taskId,
                    observations = request.observations,
                    result = request.result
                )
            ),
            performedAt = request.performedAt,
            reportTemplateCode = request.reportTemplateCode
        )
    }

    @Transactional
    fun completeTasks(
        request: CompleteTasksRequest
    ): CompletedInspectionReportResponse {
        return completeTasksInternal(
            items = request.items,
            performedAt = request.performedAt,
            reportTemplateCode = request.reportTemplateCode
        )
    }

    //method
    private fun completeTasksInternal(
        items: List<CompleteTaskItemRequest>,
        performedAt: LocalDateTime?,
        reportTemplateCode: String
    ): CompletedInspectionReportResponse {
        require(items.isNotEmpty()) {
            "At least one task must be provided"
        }

        val effectivePerformedAt = performedAt ?: LocalDateTime.now()

        val taskIds = items.map { it.taskId }

        if (taskIds.distinct().size != taskIds.size) {
            throw IllegalArgumentException("Duplicate task ids are not allowed")
        }

        val tasks = inspectionTaskRepository.findAllById(taskIds)

        if (tasks.size != taskIds.size) {
            val foundTaskIds = tasks.mapNotNull { it.id }.toSet()
            val missingTaskIds = taskIds.filterNot { it in foundTaskIds }

            throw NotFoundException("Tasks not found: $missingTaskIds")
        }

        val tasksById = tasks.associateBy { it.id!! }

        val alreadyReportedTaskIds =
            inspectionReportItemRepository.findExistingTaskIds(taskIds)

        if (alreadyReportedTaskIds.isNotEmpty()) {
            throw IllegalStateException(
                "Some tasks already have inspection report items: $alreadyReportedTaskIds"
            )
        }

        items.forEach { itemRequest ->
            val task = tasksById[itemRequest.taskId]
                ?: throw NotFoundException("Task ${itemRequest.taskId} not found")

            validateTaskCanBeCompleted(itemRequest.taskId, task.status)

            if (task.assignedEngineer == null) {
                throw IllegalStateException("Task ${itemRequest.taskId} has no assigned engineer")
            }

            if (itemRequest.observations.isBlank()) {
                throw IllegalArgumentException("Observations are required for task ${itemRequest.taskId}")
            }
        }

        val reportType = if (items.size == 1) {
            InspectionReportType.SINGLE_EQUIPMENT
        } else {
            InspectionReportType.MULTI_EQUIPMENT
        }

        val report = inspectionReportRepository.save(
            InspectionReport(
                reportNumber = generateReportNumber(
                    performedAt = effectivePerformedAt
                ),
                reportType = reportType,
                status = InspectionReportStatus.DRAFT,
                reportTemplateCode = reportTemplateCode
            )
        )

        val reportItems = items.map { itemRequest ->
            val task = tasksById[itemRequest.taskId]
                ?: throw NotFoundException("Task ${itemRequest.taskId} not found")

            InspectionReportItem(
                inspectionReport = report,
                inspectionTask = task,
                engineer = task.assignedEngineer!!,
                observations = itemRequest.observations.trim(),
                result = itemRequest.result,
                performedAt = effectivePerformedAt
            )
        }

        val savedItems = inspectionReportItemRepository.saveAll(reportItems)

        val completedTasks = items.map { itemRequest ->
            val task = tasksById[itemRequest.taskId]
                ?: throw NotFoundException("Task ${itemRequest.taskId} not found")

            task.status = TaskStatus.COMPLETED
            task.completedAt = effectivePerformedAt
            task
        }

        inspectionTaskRepository.saveAll(completedTasks)

        val fileName = "proces-verbal-${report.reportNumber}.pdf"

        val pdfBytes = pdfGenerator.generateInspectionReportPdf(
            report = report,
            items = savedItems
        )

        reportEmailService.sendInspectionReportEmail(
            items = savedItems,
            pdfFileName = fileName,
            pdfBytes = pdfBytes
        )

        report.fileName = fileName
        report.contentType = MediaType.APPLICATION_PDF_VALUE
        report.fileSizeBytes = pdfBytes.size.toLong()
        report.generatedAt = LocalDateTime.now()
        report.finalizedAt = LocalDateTime.now()
        report.status = InspectionReportStatus.FINALIZED

        inspectionReportRepository.save(report)

        return report.toCompletedResponse(
            items = savedItems,
            emailSent = true
        )
    }
}
