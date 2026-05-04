package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.*
import swbg.solutions.com.maintenanceapp.service.InspectionReportService
import java.util.*

@RestController
@RequestMapping("/api/inspection-reports")
class InspectionReportController(
    private val inspectionReportService: InspectionReportService
) {

    @PostMapping("/tasks/{taskId}/complete")
    @ResponseStatus(HttpStatus.CREATED)
    fun completeTask(
        @PathVariable taskId: UUID,
        @RequestBody @Valid request: CompleteTaskRequest
    ): CompletedInspectionReportResponse {
        return inspectionReportService.completeTask(taskId, request)
    }

    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.CREATED)
    fun completeTasks(
        @RequestBody @Valid request: CompleteTasksRequest
    ): CompletedInspectionReportResponse {
        return inspectionReportService.completeTasks(request)
    }

    /**
     * Create a report manually with one or many items.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReport(
        @RequestBody @Valid request: CreateInspectionReportRequest
    ): InspectionReportResponse {
        return inspectionReportService.createReport(request)
    }

    /**
     * Get the report created for a task.
     */
    @GetMapping("/tasks/{taskId}")
    fun getByTaskId(
        @PathVariable taskId: UUID
    ): InspectionReportResponse {
        return inspectionReportService.getByTaskId(taskId)
    }

    /**
     * Download/regenerate PDF for the report created from a task.
     */
    @GetMapping(
        "/tasks/{taskId}/pdf",
        produces = [MediaType.APPLICATION_PDF_VALUE]
    )
    fun downloadPdfByTaskId(
        @PathVariable taskId: UUID
    ): ResponseEntity<ByteArray> {
        val (fileName, pdfBytes) = inspectionReportService.generatePdfForExistingReport(taskId)

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(pdfBytes)
    }

    /**
     * Get report details by report id.
     */
    @GetMapping("/{reportId}")
    fun getById(
        @PathVariable reportId: UUID
    ): InspectionReportResponse {
        return inspectionReportService.getById(reportId)
    }

    /**
     * Download/regenerate PDF for an existing report.
     */
    @GetMapping(
        "/{reportId}/pdf",
        produces = [MediaType.APPLICATION_PDF_VALUE]
    )
    fun downloadPdfByReportId(
        @PathVariable reportId: UUID
    ): ResponseEntity<ByteArray> {
        val (fileName, pdfBytes) = inspectionReportService.generatePdfForExistingReport(reportId)

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(pdfBytes)
    }

    /**
     * Finalize report after PDF was generated/uploaded.
     */
    @PatchMapping("/{reportId}/finalize")
    fun finalizeReport(
        @PathVariable reportId: UUID,
        @RequestBody @Valid request: FinalizeInspectionReportRequest
    ): InspectionReportResponse {
        return inspectionReportService.finalizeReport(reportId, request)
    }
}