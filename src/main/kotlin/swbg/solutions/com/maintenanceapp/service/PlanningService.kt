package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.dto.AssignTaskRequest
import swbg.solutions.com.maintenanceapp.utils.EquipmentSeasonType
import swbg.solutions.com.maintenanceapp.utils.PlannerUtils
import swbg.solutions.com.maintenanceapp.utils.PlanningRequest
import swbg.solutions.com.maintenanceapp.utils.PlanningResult
import java.time.LocalDate
import java.util.*

@Service
class PlanningService(
    private val equipmentService: EquipmentService,
    private val engineerService: EngineerService,
    private val calendarConstraintService: CalendarConstraintService,
    private val maintenanceTaskService: MaintenanceTaskService,
) {
    @Transactional
    fun generatePlan(request: PlanningRequest): PlanningResult {
        require(!request.endDate.isBefore(request.startDate)) {
            "endDate cannot be before startDate"
        }

        val requestRange = DateRange(request.startDate, request.endDate)

        var generated = 0
        val skipped = mutableListOf<String>()

        val rules = equipmentService.getActiveRules()

        for (rule in rules) {
            val equipment = rule.equipment

            val equipmentId = requireNotNull(equipment.id) {
                "Equipment ${equipment.code} has null id"
            }

            val assignedEngineerId = requireNotNull(equipment.assignedEngineer.id) {
                "Equipment ${equipment.code} has no assigned engineer"
            }

            if (rule.recurrencePerYear <= 0) {
                skipped += "${equipment.code}: recurrencePerYear must be positive"
                continue
            }

            for (year in request.startDate.year..request.endDate.year) {
                val eligibleDatesForYear = eligibleDatesForYear(
                    year = year,
                    seasonType = equipment.seasonType
                )

                if (eligibleDatesForYear.isEmpty()) {
                    skipped += "${equipment.code}: no eligible dates in $year for ${equipment.seasonType}"
                    continue
                }

                val targetDatesForYear = PlannerUtils.evenlyDistributedDates(
                    eligibleDates = eligibleDatesForYear,
                    targetCount = rule.recurrencePerYear
                )

                val targetDatesInRequestedRange = targetDatesForYear
                    .filter { date -> requestRange.contains(date) }

                if (targetDatesInRequestedRange.isEmpty()) {
                    skipped += "${equipment.code}: no planned dates in requested range for $year"
                    continue
                }

                for (targetDate in targetDatesInRequestedRange) {
                    if (maintenanceTaskService.hasTaskForEquipmentOnDate(equipmentId, targetDate)) {
                        continue
                    }

                    if (!hasEngineerCapacity(assignedEngineerId, targetDate)) {
                        skipped += "${equipment.code}: assigned engineer has no capacity on $targetDate"
                        continue
                    }

                    maintenanceTaskService.create(
                        AssignTaskRequest(
                            equipmentId = equipmentId,
                            assignedEngineerId = assignedEngineerId,
                            scheduledDate = targetDate
                        ),
                        generatedByPlanner = true
                    )

                    generated++
                }
            }
        }

        return PlanningResult(
            generatedTasks = generated,
            skippedEquipments = skipped
        )
    }

    private fun eligibleDatesForYear(
        year: Int,
        seasonType: EquipmentSeasonType
    ): List<LocalDate> {
        return dateSequence(
            start = LocalDate.of(year, 1, 1),
            end = LocalDate.of(year, 12, 31)
        )
            .filter { date -> seasonType.isActiveOn(date) }
            .filter { date -> calendarConstraintService.isWorkingDayForEngineer(date) }
            .toList()
    }

    private fun hasEngineerCapacity(engineerId: UUID, date: LocalDate): Boolean {
        val maxTasks = engineerService.getMaxTasksPerDay(engineerId)
        val currentTasks = maintenanceTaskService.countActiveTasksForEngineerOnDate(engineerId, date)

        return currentTasks < maxTasks
    }

    private fun dateSequence(start: LocalDate, end: LocalDate): Sequence<LocalDate> {
        return generateSequence(start) { current ->
            current.plusDays(1).takeIf { next -> !next.isAfter(end) }
        }
    }

    private data class DateRange(
        val start: LocalDate,
        val end: LocalDate
    ) {
        init {
            require(!end.isBefore(start)) {
                "DateRange end cannot be before start"
            }
        }

        fun contains(date: LocalDate): Boolean {
            return !date.isBefore(start) && !date.isAfter(end)
        }
    }
}
