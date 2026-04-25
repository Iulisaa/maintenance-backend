package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.MaintenanceTask
import swbg.solutions.com.maintenanceapp.utils.TaskStatus
import java.time.LocalDate
import java.util.*

interface MaintenanceTaskRepository : JpaRepository<MaintenanceTask, UUID> {
    fun findAllByAssignedEngineerIdAndScheduledDateAndStatus(
        engineerId: UUID,
        scheduledDate: LocalDate,
        status: TaskStatus
    ): List<MaintenanceTask>

    fun findAllByAssignedEngineerIdAndStatus(
        engineerId: UUID,
        status: TaskStatus
    ): List<MaintenanceTask>

    fun findAllByScheduledDateBetween(start: LocalDate, end: LocalDate): List<MaintenanceTask>
    fun countByAssignedEngineerIdAndScheduledDateAndStatusNot(
        engineerId: UUID,
        scheduledDate: LocalDate,
        status: TaskStatus
    ): Long

    fun existsByEquipmentIdAndScheduledDate(equipmentId: UUID, scheduledDate: LocalDate): Boolean
}