package swbg.solutions.com.maintenanceapp.entity

import jakarta.persistence.*
import swbg.solutions.com.maintenanceapp.dto.MaintenanceResult
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "maintenance_reports",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_maintenance_reports_task_id",
            columnNames = ["task_id"]
        )
    ]
)
class MaintenanceReport(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: MaintenanceTask,

    @Column(nullable = false, columnDefinition = "TEXT")
    val observations: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val result: MaintenanceResult,

    @Column(name = "performed_at", nullable = false)
    val performedAt: LocalDateTime,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
)