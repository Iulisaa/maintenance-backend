package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import swbg.solutions.com.maintenanceapp.entity.InspectionResult
import swbg.solutions.com.maintenanceapp.entity.InspectionTask
import swbg.solutions.com.maintenanceapp.entity.TaskStatus
import java.time.LocalDate
import java.util.*

interface InspectionTaskRepository : JpaRepository<InspectionTask, UUID> {
    @Query(
        """
    select distinct task
    from InspectionTask task
    join fetch task.equipment equipment
    left join fetch task.assignedEngineer engineer
    left join fetch task.reportItem reportItem
    where engineer.id = :engineerId
      and task.plannedDate between :startDate and :endDate
    order by task.plannedDate asc, equipment.name asc
"""
    )
    fun findForEngineerInRange(
        @Param("engineerId") engineerId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<InspectionTask>


    @Query(
        """
    select distinct task
    from InspectionTask task
    join fetch task.equipment equipment
    left join fetch task.assignedEngineer engineer
    left join fetch task.reportItem reportItem
    where engineer.id = :engineerId
      and task.plannedDate between :startDate and :endDate
      and task.status in :statuses
    order by task.plannedDate asc, equipment.name asc
"""
    )
    fun findForEngineerInRangeAndStatusIn(
        @Param("engineerId") engineerId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("statuses") statuses: Collection<TaskStatus>
    ): List<InspectionTask>


    @Query(
        """
    select distinct task
    from InspectionTask task
    join fetch task.equipment equipment
    left join fetch task.assignedEngineer engineer
    left join fetch task.reportItem reportItem
    where engineer.id = :engineerId
      and task.plannedDate between :startDate and :endDate
      and reportItem.result = :result
    order by task.plannedDate asc, equipment.name asc
"""
    )
    fun findForEngineerInRangeAndResult(
        @Param("engineerId") engineerId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("result") result: InspectionResult
    ): List<InspectionTask>

    fun findAllByPlannedDateBetween(
        start: LocalDate,
        end: LocalDate
    ): List<InspectionTask>

    fun countByAssignedEngineerIdAndPlannedDateAndStatusNot(
        engineerId: UUID,
        plannedDate: LocalDate,
        status: TaskStatus
    ): Long

    /**
     * This is okay for manual duplicate checks by date,
     * but for generated tasks prefer existsByEquipmentIdAndGenerationKey.
     */
    fun existsByEquipmentIdAndPlannedDate(
        equipmentId: UUID,
        plannedDate: LocalDate
    ): Boolean

    /**
     * Use this for planner-generated tasks.
     * This is the important one for preventing duplicate generated occurrences.
     */
    fun existsByEquipmentIdAndGenerationKey(
        equipmentId: UUID,
        generationKey: String
    ): Boolean

    fun findAllByAssignedEngineerIdAndPlannedDateBetween(
        assignedEngineerId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<InspectionTask>


}
