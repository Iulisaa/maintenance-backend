package swbg.solutions.com.maintenanceapp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import swbg.solutions.com.maintenanceapp.utils.PlannerUtils
import java.time.LocalDate

class PlannerUtilsTest {

    @Test
    fun `should distribute dates evenly`() {
        val start = LocalDate.of(2026, 1, 1)
        val candidates = (0..11).map { start.plusDays(it.toLong()) }

        val result = PlannerUtils.evenlyDistributedDates(candidates, 4)

        assertEquals(4, result.size)
        assertEquals(start, result[0])
    }
}
