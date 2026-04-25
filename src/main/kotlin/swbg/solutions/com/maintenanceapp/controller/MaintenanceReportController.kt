package swbg.solutions.com.maintenanceapp.controller

import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import swbg.solutions.com.maintenanceapp.dto.CompleteTaskRequest
import swbg.solutions.com.maintenanceapp.dto.MaintenanceReportResponse
import swbg.solutions.com.maintenanceapp.service.MaintenanceReportService
import java.util.*

@RestController
@RequestMapping("/api/reports")
class MaintenanceReportController(
    private val maintenanceReportService: MaintenanceReportService
) {
    @PostMapping("/tasks/{taskId}/complete")
    @ResponseStatus(HttpStatus.CREATED)
    fun completeTask(
        @PathVariable taskId: UUID,
        @RequestBody @Valid request: CompleteTaskRequest
    ): MaintenanceReportResponse =
        maintenanceReportService.completeTask(taskId, request)

    @PostMapping(
        "/tasks/{taskId}/pdf",
        produces = [MediaType.APPLICATION_PDF_VALUE]
    )
    fun generatePdf(
        @PathVariable taskId: UUID,
        @RequestBody @Valid request: CompleteTaskRequest
    ): ResponseEntity<ByteArray> {
        val (fileName, pdfBytes) = maintenanceReportService.completeTaskAndGeneratePdf(taskId, request)

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(pdfBytes)
    }
}