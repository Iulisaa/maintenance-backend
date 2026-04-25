package swbg.solutions.com.maintenanceapp.utils

import java.time.LocalDate
import java.time.Month

enum class EquipmentSeasonType(
    val activeMonths: List<Month>
) {
    HEAT(
        listOf(
            Month.NOVEMBER,
            Month.DECEMBER,
            Month.JANUARY,
            Month.FEBRUARY,
            Month.MARCH,
            Month.APRIL
        )
    ),

    COLD(
        listOf(
            Month.MAY,
            Month.JUNE,
            Month.JULY,
            Month.AUGUST,
            Month.SEPTEMBER,
            Month.OCTOBER
        )
    ),

    UNIVERSAL(Month.entries.toList());

    fun isActiveOn(date: LocalDate): Boolean {
        return date.month in activeMonths
    }
}