package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "inspection_report_items",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_inspection_report_items_task_id",
            columnNames = ["inspection_task_id"]
        )
    ],
    indexes = [
        Index(name = "idx_inspection_report_items_report_id", columnList = "inspection_report_id"),
        Index(name = "idx_inspection_report_items_task_id", columnList = "inspection_task_id"),
        Index(name = "idx_inspection_report_items_engineer_id", columnList = "engineer_id"),
        Index(name = "idx_inspection_report_items_result", columnList = "result")
    ]
)
class InspectionReportItem(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_report_id", nullable = false)
    var inspectionReport: InspectionReport,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_task_id", nullable = false)
    var inspectionTask: InspectionTask,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "engineer_id", nullable = false)
    var engineer: Engineer,

    @Column(nullable = false, columnDefinition = "TEXT")
    var observations: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var result: InspectionResult,

    @Column(name = "performed_at", nullable = false)
    var performedAt: LocalDateTime = LocalDateTime.now()

) : AuditableEntity()
