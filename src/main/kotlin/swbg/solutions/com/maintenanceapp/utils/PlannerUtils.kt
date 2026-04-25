package swbg.solutions.com.maintenanceapp.utils

import java.time.LocalDate
import kotlin.math.roundToInt

object PlannerUtils {
    fun evenlyDistributedDates(
        eligibleDates: List<LocalDate>,
        targetCount: Int
    ): List<LocalDate> {
        require(targetCount >= 0) {
            "targetCount cannot be negative"
        }

        if (targetCount == 0 || eligibleDates.isEmpty()) {
            return emptyList()
        }

        if (targetCount >= eligibleDates.size) {
            return eligibleDates
        }

        if (targetCount == 1) {
            return listOf(eligibleDates[eligibleDates.size / 2])
        }

        val lastIndex = eligibleDates.lastIndex

        return (0 until targetCount)
            .map { index ->
                val selectedIndex = ((index.toDouble() * lastIndex) / (targetCount - 1)).roundToInt()
                eligibleDates[selectedIndex]
            }
            .distinct()
    }
}
