package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "engineers",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_engineers_email",
            columnNames = ["email"]
        )
    ],
    indexes = [
        Index(name = "idx_engineers_active", columnList = "active")
    ]
)
class Engineer(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String,

    @Column(nullable = false, length = 255)
    var email: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(name = "max_tasks_per_day", nullable = false)
    var maxTasksPerDay: Int = 5

) : AuditableEntity()
