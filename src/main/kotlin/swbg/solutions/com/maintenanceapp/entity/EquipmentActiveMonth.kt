package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Embeddable
data class EquipmentActiveMonthId(

    @Column(name = "equipment_id", nullable = false)
    var equipmentId: UUID? = null,

    @Column(name = "month_number", nullable = false)
    var monthNumber: Int? = null

) : Serializable

@Entity
@Table(
    name = "equipment_active_months",
    indexes = [
        Index(name = "idx_equipment_active_months_month_number", columnList = "month_number")
    ]
)
class EquipmentActiveMonth(

    @EmbeddedId
    var id: EquipmentActiveMonthId = EquipmentActiveMonthId(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id", nullable = false)
    var equipment: Equipment

)
