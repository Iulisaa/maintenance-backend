package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.Holiday
import java.time.LocalDate
import java.util.UUID

interface HolidayRepository : JpaRepository<Holiday, UUID> {
    fun existsByHolidayDate(holidayDate: LocalDate): Boolean
    fun findAllByHolidayDateBetween(start: LocalDate, end: LocalDate): List<Holiday>
}