package swbg.solutions.com.maintenanceapp.service

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate

@Service
class CalendarConstraintService(
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val holidays: MutableSet<LocalDate> = mutableSetOf()

    @PostConstruct
    fun loadHolidays() {
        val resource = ClassPathResource("holidays.csv")

        if (!resource.exists()) {
            log.warn("Holiday CSV not found at $resource No holidays will be skipped.")
            return
        }

        resource.inputStream.bufferedReader().useLines { lines ->
            lines
                .drop(1)
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { line ->
                    val columns = line.split(",")

                    if (columns.isEmpty()) {
                        return@forEach
                    }

                    val dateText = columns[0].trim()

                    try {
                        holidays += LocalDate.parse(dateText)
                    } catch (ex: Exception) {
                        log.warn("Invalid holiday CSV row ignored: {}", line)
                    }
                }
        }

        log.info("Loaded {} holiday dates from compliance/holidays.csv", holidays.size)
    }

    fun isHoliday(date: LocalDate): Boolean {
        return date in holidays
    }

    fun isWorkingDayForEngineer(date: LocalDate): Boolean {
        return !isWeekend(date) && !isHoliday(date)
    }

    fun isWeekend(date: LocalDate): Boolean {
        return date.dayOfWeek == DayOfWeek.SATURDAY ||
                date.dayOfWeek == DayOfWeek.SUNDAY
    }
}
