package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import swbg.solutions.com.maintenanceapp.utils.EquipmentSeasonType
import java.util.*

@Entity
@Table(name = "equipments")
class Equipment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var code: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var seasonType: EquipmentSeasonType,

    @Column
    var serialNumber: String? = null,

    @Column
    var notes: String? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_engineer_id", nullable = false)
    var assignedEngineer: Engineer
)