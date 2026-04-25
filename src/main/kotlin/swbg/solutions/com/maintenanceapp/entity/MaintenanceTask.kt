package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import swbg.solutions.com.maintenanceapp.utils.TaskStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "maintenance_tasks")
class MaintenanceTask(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    var equipment: Equipment,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_engineer_id", nullable = false)
    var assignedEngineer: Engineer,

    @Column(nullable = false)
    var scheduledDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TaskStatus = TaskStatus.PLANNED,

    @Column(nullable = false)
    var generatedByPlanner: Boolean = true,

    @Column
    var completedAt: LocalDateTime? = null
)