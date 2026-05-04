package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "equipment_category")
class EquipmentCategory(

    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(nullable = false, length = 120)
    var name: String,

    @Column(length = 500)
    var description: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
