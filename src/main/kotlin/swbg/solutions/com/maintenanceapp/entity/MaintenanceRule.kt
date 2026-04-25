package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "maintenance_rules")
class MaintenanceRule(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false, unique = true)
    var equipment: Equipment,

    @Column(nullable = false)
    var recurrencePerYear: Int,

    @Column(nullable = false)
    var estimatedDurationMinutes: Int,

    @Column(nullable = false)
    var reportTemplateCode: String = "DEFAULT"
)