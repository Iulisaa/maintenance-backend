package swbg.solutions.com.maintenanceapp.mapper

import swbg.solutions.com.maintenanceapp.dto.InspectionReportItemResponse
import swbg.solutions.com.maintenanceapp.dto.InspectionReportResponse
import swbg.solutions.com.maintenanceapp.entity.InspectionReport
import swbg.solutions.com.maintenanceapp.entity.InspectionReportItem

fun InspectionReportItem.toResponse(): InspectionReportItemResponse {
    return InspectionReportItemResponse(
        id = requireNotNull(id),
        inspectionTask = inspectionTask.toSummaryResponse(),
        engineer = engineer.toSummaryResponse(),
        result = result,
        observations = observations,
        performedAt = performedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun InspectionReport.toResponse(items: List<InspectionReportItem>): InspectionReportResponse {
    return InspectionReportResponse(
        id = requireNotNull(id),
        reportNumber = reportNumber,
        reportType = reportType,
        status = status,
        reportTemplateCode = reportTemplateCode,
        fileName = fileName,
        contentType = contentType,
        storagePath = storagePath,
        fileSizeBytes = fileSizeBytes,
        generatedAt = generatedAt,
        finalizedAt = finalizedAt,
        items = items.map { it.toResponse() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        inspectionReportId = id
    )
}
