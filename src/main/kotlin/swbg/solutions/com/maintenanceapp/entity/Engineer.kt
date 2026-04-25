package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "engineers")
class Engineer(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    var fullName: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false)
    var maxTasksPerDay: Int = 5
)