package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.EquipmentCategory
import java.util.*

interface EquipmentCategoryRepository : JpaRepository<EquipmentCategory, UUID> {
    fun findAllByActiveTrueOrderByNameAsc(): List<EquipmentCategory>
    fun existsByNameIgnoreCase(name: String): Boolean
}
