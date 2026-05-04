package swbg.solutions.com.maintenanceapp.utils

import java.time.LocalDate
import kotlin.math.roundToInt

object PlannerUtils {
    fun evenlyDistributedDates(
        eligibleDates: List<LocalDate>,
        targetCount: Int
    ): List<LocalDate> {
        require(targetCount > 0) {
            "targetCount must be greater than 0"
        }

        if (eligibleDates.isEmpty()) {
            return emptyList()
        }

        if (targetCount >= eligibleDates.size) {
            return eligibleDates
        }

        if (targetCount == 1) {
            return listOf(eligibleDates[eligibleDates.size / 2])
        }

        val lastIndex = eligibleDates.lastIndex
        val step = lastIndex.toDouble() / (targetCount - 1)

        return (0 until targetCount)
            .map { index ->
                val dateIndex = (index * step).roundToInt()
                eligibleDates[dateIndex]
            }
            .distinct()
    }
}
