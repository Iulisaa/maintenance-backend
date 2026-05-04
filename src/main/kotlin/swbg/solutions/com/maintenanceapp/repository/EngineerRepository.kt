package swbg.solutions.com.maintenanceapp.repository

import org.springframework.data.jpa.repository.JpaRepository
import swbg.solutions.com.maintenanceapp.entity.Engineer
import java.util.*

interface EngineerRepository : JpaRepository<Engineer, UUID> {
    fun findAllByActiveTrue(): List<Engineer>

    fun findAllByActiveFalse(): List<Engineer>

    fun existsByEmailIgnoreCaseAndIdNot(
        email: String,
        id: UUID
    ): Boolean
}
