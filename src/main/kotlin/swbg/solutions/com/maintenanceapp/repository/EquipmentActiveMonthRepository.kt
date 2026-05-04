package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.EquipmentActiveMonth
import swbg.solutions.com.maintenanceapp.entity.EquipmentActiveMonthId
import java.util.*

interface EquipmentActiveMonthRepository : JpaRepository<EquipmentActiveMonth, EquipmentActiveMonthId> {

    fun findAllByEquipmentId(equipmentId: UUID): List<EquipmentActiveMonth>

    fun deleteAllByEquipmentId(equipmentId: UUID)
}
