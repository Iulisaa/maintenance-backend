package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.MaintenanceRule
import java.util.UUID

interface MaintenanceRuleRepository : JpaRepository<MaintenanceRule, UUID> {
    fun findByEquipmentId(equipmentId: UUID): MaintenanceRule?
    fun findAllByEquipmentActiveTrue(): List<MaintenanceRule>
}