package swbg.solutions.com.maintenanceapp.utils

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import swbg.solutions.com.maintenanceapp.dto.PlanningRequest
import swbg.solutions.com.maintenanceapp.service.PlanningService
import java.time.LocalDate
import java.time.ZoneId

@Component
class PlanningScheduler(
    private val planningService: PlanningService
) {
    private val log = LoggerFactory.getLogger(PlanningScheduler::class.java)
    private val zoneId = ZoneId.of("Europe/Bucharest")

    @Scheduled(cron = "0 * * * * *", zone = "Europe/Bucharest")
    fun generateNextThirtyDaysPlanning() {
        val today = LocalDate.now(zoneId)

        val request = PlanningRequest(
            startDate = today,
            endDate = today.plusDays(30)
        )

        log.info(
            "Automatic planning generation started. startDate={}, endDate={}",
            request.startDate,
            request.endDate
        )

        val result = planningService.generatePlan(request)

        log.info(
            "Automatic planning generation finished. generatedTasks={}, skippedCount={}, skippedEquipments={}",
            result.generatedTasks,
            result.skippedEquipments.size,
            result.skippedEquipments
        )
    }
}