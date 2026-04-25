package swbg.solutions.com.maintenanceapp.service

import org.springframework.stereotype.Service
import swbg.solutions.com.maintenanceapp.repository.HolidayRepository
import java.time.DayOfWeek
import java.time.LocalDate

@Service
class CalendarConstraintService(
    private val holidayRepository: HolidayRepository,
) {
    fun isWeekend(date: LocalDate): Boolean =
        date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY

    fun isHoliday(date: LocalDate): Boolean =
        holidayRepository.existsByHolidayDate(date)

    fun isWorkingDayForEngineer(date: LocalDate): Boolean =
        !isWeekend(date) && !isHoliday(date)
}