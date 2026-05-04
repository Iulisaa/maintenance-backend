package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import swbg.solutions.com.maintenanceapp.dto.EquipmentInspectionHistoryResponse
import swbg.solutions.com.maintenanceapp.entity.InspectionReportItem
import java.util.*

interface InspectionReportItemRepository : JpaRepository<InspectionReportItem, UUID> {
    @Query(
        """
    select item
    from InspectionReportItem item
    join fetch item.inspectionReport report
    join fetch item.inspectionTask task
    where task.id in :taskIds
    """
    )
    fun findByInspectionTaskIdsWithReport(
        @Param("taskIds") taskIds: Collection<UUID>
    ): List<InspectionReportItem>

    fun existsByInspectionTaskId(inspectionTaskId: UUID): Boolean

    fun findByInspectionTaskId(inspectionTaskId: UUID): InspectionReportItem?

    fun findAllByInspectionReportId(inspectionReportId: UUID): List<InspectionReportItem>

    @Query(
        """
        select item.inspectionTask.id
        from InspectionReportItem item
        where item.inspectionTask.id in :taskIds
    """
    )
    fun findExistingTaskIds(
        @Param("taskIds") taskIds: Collection<UUID>
    ): List<UUID>

    @Query(
        """
        SELECT new swbg.solutions.com.maintenanceapp.dto.EquipmentInspectionHistoryResponse(
            iri.performedAt,
            iri.observations,
            iri.result,
            eng.fullName,
            report.reportNumber,
            report.fileName
        )
        FROM InspectionReportItem iri
        JOIN iri.inspectionTask task
        LEFT JOIN iri.engineer eng
        LEFT JOIN iri.inspectionReport report
        WHERE task.equipment.id = :equipmentId
          AND task.id <> :currentTaskId
          AND task.status = swbg.solutions.com.maintenanceapp.entity.TaskStatus.COMPLETED
        ORDER BY iri.performedAt DESC
        """
    )
    fun findEquipmentHistoryForTask(
        @Param("equipmentId") equipmentId: UUID?,
        @Param("currentTaskId") currentTaskId: UUID?
    ): List<EquipmentInspectionHistoryResponse>
}
