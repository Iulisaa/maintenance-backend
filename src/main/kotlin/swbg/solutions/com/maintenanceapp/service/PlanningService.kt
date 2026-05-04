package swbg.solutions.com.maintenanceapp.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import swbg.solutions.com.maintenanceapp.dto.PlanningRequest
import swbg.solutions.com.maintenanceapp.dto.PlanningResult
import swbg.solutions.com.maintenanceapp.entity.Equipment
import swbg.solutions.com.maintenanceapp.repository.EquipmentActiveMonthRepository
import swbg.solutions.com.maintenanceapp.utils.PlannerUtils
import java.time.LocalDate
import java.util.*


@Service
class PlanningService(
    private val equipmentService: EquipmentService,
    private val engineerService: EngineerService,
    private val calendarConstraintService: CalendarConstraintService,
    private val inspectionTaskService: InspectionTaskService,
    private val equipmentActiveMonthRepository: EquipmentActiveMonthRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun generatePlan(request: PlanningRequest): PlanningResult {
        return generatePlanInternal(
            request = request,
            onlyEquipmentId = null
        )
    }

    @Transactional
    fun generatePlanForEquipment(
        equipmentId: UUID,
        request: PlanningRequest
    ): PlanningResult {
        log.info(
            "Generating planning for equipmentId={}, from {} to {}",
            equipmentId,
            request.startDate,
            request.endDate
        )

        val result = generatePlanInternal(
            request = request,
            onlyEquipmentId = equipmentId
        )

        log.info(
            "Finished generating planning for equipmentId={}. generatedTasks={}, skippedCount={}",
            equipmentId,
            result.generatedTasks,
            result.skippedEquipments.size
        )

        return result
    }

    private fun generatePlanInternal(
        request: PlanningRequest,
        onlyEquipmentId: UUID?
    ): PlanningResult {
        require(!request.endDate.isBefore(request.startDate)) {
            "endDate cannot be before startDate"
        }

        val requestRange = DateRange(request.startDate, request.endDate)

        var generated = 0
        val skipped = mutableListOf<String>()

        val equipments = loadTargetEquipments(onlyEquipmentId)

        if (onlyEquipmentId != null && equipments.isEmpty()) {
            return PlanningResult(
                generatedTasks = 0,
                skippedEquipments = listOf("Equipment $onlyEquipmentId was not found or is not active")
            )
        }

        for (equipment in equipments) {
            val equipmentId = requireNotNull(equipment.id) {
                "Equipment ${equipment.code} has null id"
            }

            if (equipment.frequencyPerYear <= 0) {
                skipped += "${equipment.code}: frequencyPerYear must be positive"
                continue
            }

            val activeMonths = equipmentActiveMonthRepository
                .findAllByEquipmentId(equipmentId)
                .mapNotNull { it.id.monthNumber }
                .toSet()

            if (activeMonths.isEmpty()) {
                skipped += "${equipment.code}: no active months configured"
                continue
            }

            val assignedEngineer = equipment.defaultEngineer

            if (assignedEngineer == null) {
                skipped += "${equipment.code}: no default engineer configured"
                continue
            }

            val assignedEngineerId = requireNotNull(assignedEngineer.id) {
                "Default engineer for equipment ${equipment.code} has null id"
            }

            for (year in request.startDate.year..request.endDate.year) {
                val eligibleDatesForYear = eligibleDatesForYear(
                    year = year,
                    activeMonths = activeMonths
                )

                if (eligibleDatesForYear.isEmpty()) {
                    skipped += "${equipment.code}: no eligible dates in $year"
                    continue
                }

                val targetDatesForYear = PlannerUtils.evenlyDistributedDates(
                    eligibleDates = eligibleDatesForYear,
                    targetCount = equipment.frequencyPerYear
                )

                val occurrencesForYear = targetDatesForYear.mapIndexed { index, plannedDate ->
                    PlannedOccurrence(
                        occurrenceNumber = index + 1,
                        plannedDate = plannedDate,
                        generationKey = buildGenerationKey(
                            equipmentId = equipmentId,
                            year = year,
                            occurrenceNumber = index + 1
                        )
                    )
                }

                val occurrencesInRequestedRange = occurrencesForYear
                    .filter { occurrence -> requestRange.contains(occurrence.plannedDate) }

                if (occurrencesInRequestedRange.isEmpty()) {
                    skipped += "${equipment.code}: no planned dates in requested range for $year"
                    continue
                }

                for (occurrence in occurrencesInRequestedRange) {
                    if (
                        inspectionTaskService.hasGeneratedTaskForEquipment(
                            equipmentId = equipmentId,
                            generationKey = occurrence.generationKey
                        )
                    ) {
                        continue
                    }

                    if (!hasEngineerCapacity(assignedEngineerId, occurrence.plannedDate)) {
                        skipped += "${equipment.code}: default engineer has no capacity on ${occurrence.plannedDate}"
                        continue
                    }

                    log.info(
                        "Creating generated inspection task. equipmentId={}, equipmentCode={}, assignedEngineerId={}, plannedDate={}, occurrenceNumber={}, generationKey={}",
                        equipmentId,
                        equipment.code,
                        assignedEngineerId,
                        occurrence.plannedDate,
                        occurrence.occurrenceNumber,
                        occurrence.generationKey
                    )

                    inspectionTaskService.createGeneratedTask(
                        equipmentId = equipmentId,
                        plannedDate = occurrence.plannedDate,
                        occurrenceNumber = occurrence.occurrenceNumber,
                        generationKey = occurrence.generationKey
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

    private fun loadTargetEquipments(onlyEquipmentId: UUID?): List<Equipment> {
        return if (onlyEquipmentId != null) {
            val equipment = equipmentService.getEntityById(onlyEquipmentId)
            if (equipment.active) listOf(equipment) else emptyList()
        } else {
            equipmentService.getAllActive()
        }
    }

    private fun eligibleDatesForYear(
        year: Int,
        activeMonths: Set<Int>
    ): List<LocalDate> {
        return dateSequence(
            start = LocalDate.of(year, 1, 1),
            end = LocalDate.of(year, 12, 31)
        )
            .filter { date -> date.monthValue in activeMonths }
            .filter { date -> calendarConstraintService.isWorkingDayForEngineer(date) }
            .toList()
    }

    private fun hasEngineerCapacity(
        engineerId: UUID,
        date: LocalDate
    ): Boolean {
        val maxTasks = engineerService.getMaxTasksPerDay(engineerId)
        val currentTasks = inspectionTaskService.countActiveTasksForEngineerOnDate(
            engineerId = engineerId,
            date = date
        )

        return currentTasks < maxTasks
    }

    private fun buildGenerationKey(
        equipmentId: UUID,
        year: Int,
        occurrenceNumber: Int
    ): String {
        return "equipment-$equipmentId-year-$year-occurrence-$occurrenceNumber"
    }

    private fun dateSequence(
        start: LocalDate,
        end: LocalDate
    ): Sequence<LocalDate> {
        return generateSequence(start) { current ->
            current.plusDays(1).takeIf { next -> !next.isAfter(end) }
        }
    }

    private data class PlannedOccurrence(
        val occurrenceNumber: Int,
        val plannedDate: LocalDate,
        val generationKey: String
    )

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
