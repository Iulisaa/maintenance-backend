package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.Engineer
import java.util.UUID

interface EngineerRepository : JpaRepository<Engineer, UUID> {
    fun findAllByActiveTrue(): List<Engineer>
}