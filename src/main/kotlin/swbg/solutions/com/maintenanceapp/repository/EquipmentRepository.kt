package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.Equipment
import java.util.*

interface EquipmentRepository : JpaRepository<Equipment, UUID> {

    fun findAllByActiveTrue(): List<Equipment>

    fun findAllByActiveFalse(): List<Equipment>

}
