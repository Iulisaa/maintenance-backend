package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.MaintenanceReport
import java.util.UUID

interface MaintenanceReportRepository : JpaRepository<MaintenanceReport, UUID> {
    fun existsByTaskId(taskId: UUID): Boolean

    fun findByTaskId(taskId: UUID): MaintenanceReport?
}