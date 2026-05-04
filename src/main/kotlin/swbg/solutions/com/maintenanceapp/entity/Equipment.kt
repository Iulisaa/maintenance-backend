package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "equipments",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_equipments_code",
            columnNames = ["code"]
        )
    ],
    indexes = [
        Index(name = "idx_equipments_active", columnList = "active"),
        Index(name = "idx_equipments_season_type", columnList = "season_type"),
        Index(name = "idx_equipments_default_engineer_id", columnList = "default_engineer_id")
    ]
)
class Equipment(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(nullable = false, length = 100)
    var code: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(name = "season_type", nullable = false, length = 30)
    var seasonType: EquipmentSeasonType,

    @Column(name = "frequency_per_year", nullable = false)
    var frequencyPerYear: Int,

    @Column(name = "estimated_duration_minutes", nullable = false)
    var estimatedDurationMinutes: Int,

    @Column(name = "serial_number", length = 150)
    var serialNumber: String? = null,

    @Column(columnDefinition = "TEXT")
    var notes: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_engineer_id")
    var defaultEngineer: Engineer? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: EquipmentCategory

) : AuditableEntity()
