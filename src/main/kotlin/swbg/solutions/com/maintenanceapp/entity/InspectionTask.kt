package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "inspection_tasks",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_inspection_tasks_equipment_generation_key",
            columnNames = ["equipment_id", "generation_key"]
        )
    ],
    indexes = [
        Index(name = "idx_inspection_tasks_equipment_id", columnList = "equipment_id"),
        Index(name = "idx_inspection_tasks_assigned_engineer_id", columnList = "assigned_engineer_id"),
        Index(name = "idx_inspection_tasks_planned_date", columnList = "planned_date"),
        Index(name = "idx_inspection_tasks_status_planned_date", columnList = "status, planned_date"),
        Index(name = "idx_inspection_tasks_engineer_planned_date", columnList = "assigned_engineer_id, planned_date")
    ]
)
class InspectionTask(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    var equipment: Equipment,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_engineer_id")
    var assignedEngineer: Engineer? = null,

    @Column(name = "planned_date", nullable = false)
    var plannedDate: LocalDate,

    @Column(name = "planned_year", nullable = false)
    var plannedYear: Int,

    @Column(name = "occurrence_number")
    var occurrenceNumber: Int? = null,

    @Column(name = "generation_key", length = 150)
    var generationKey: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var source: TaskSource = TaskSource.GENERATED,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: TaskStatus = TaskStatus.PLANNED,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @OneToOne(mappedBy = "inspectionTask", fetch = FetchType.LAZY)
    var reportItem: InspectionReportItem? = null

) : AuditableEntity()
